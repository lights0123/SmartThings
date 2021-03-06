// Based on https://github.com/pstuart/smartthings-ps/blob/master/devicetypes/Control4%20Zigbee%20HA%20Dimmer.groovy
/**
 *  Control4 Outlet Dimmer
 *
 *  Copyright 2018 Ben Schattinger
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Control4 Outlet Switch", namespace: "lights0123", author: "Ben Schattinger") {
		capability "Refresh"
		capability "Polling"

		command "childOn"
		command "childOff"

		fingerprint endpointId: "02", profileId: "0104", deviceId: "0101", inClusters: "0000 0003 0004 0005 0006 0008 000A"
		fingerprint endpointId: "01", profileId: "0104", deviceId: "0101", inClusters: "0000 0003 0004 0005 0006 0008 000A"
		fingerprint endpointId: "C4", profileId: "C25D", deviceId: "0101", inClusters: "0001"
	}
   
	preferences {
	}
	simulator {
	}

	tiles {
    	valueTile("basic", "device.ip", width: 3, height: 2) {
            state("basic", label:'OK')
        }
		main "basic"
	}
}

def parse(String description) {
	log.trace(description)
	if (description?.startsWith("catchall: C25")) {
		def msg = zigbee.parse(description)
		log.trace(msg)
		def payloadhex = description.tokenize(" ").last()
		def payload = payloadhex.decodeHex()
		def x = ""
		payload.each() { x += it as char }

		log.debug("Payload is $x")
	}
}

def childOn(String id) {
	def endpoint = id.tokenize('-').last()
	def child = childDevices.find{it.deviceNetworkId == id}
	child.sendEvent(name: "switch", value: "on")
    "st cmd 0x${device.deviceNetworkId} ${endpoint} 6 1 {}"
}
def childOff(String id) {
	def endpoint = id.tokenize('-').last()
	def child = childDevices.find{it.deviceNetworkId == id}
	child.sendEvent(name: "switch", value: "off")
    "st cmd 0x${device.deviceNetworkId} ${endpoint} 6 0 {}"
}

def refresh() {
	[
			"st rattr 0x${device.deviceNetworkId} 1 6 0", "delay 100",
			"st rattr 0x${device.deviceNetworkId} 1 8 0"
	]
}

def poll(){
	log.debug "Poll is calling refresh"
	refresh()
}

def configure() {

	String zigbeeId = swapEndianHex(device.hub.zigbeeId)
	log.debug "Confuguring Reporting and Bindings."
	def configCmds = [

			//Switch Reporting
			"zcl global send-me-a-report 6 0 0x10 0 3600 {01}", "delay 500",
			"send 0x${device.deviceNetworkId} 1 1", "delay 1000",

			"zdo bind 0x${device.deviceNetworkId} 1 1 6 {${device.zigbeeId}} {}", "delay 1500",
			"zdo bind 0x${device.deviceNetworkId} 1 1 8 {${device.zigbeeId}} {}", "delay 500",
	]
	return configCmds + refresh() // send refresh cmds as part of config
}
def updated() {
	log.debug "updated()"
    installChildren()
}
def installed() {
	log.debug "installed()"
	installChildren()
    configure()
}
private installChildren() {
	for (i in 1..2) {
		try {
			log.debug "Trying to create child switch if it doesn't already exist ${i}"
            def channelID = (i == 1) ? 2 : 1
			def currentchild = getChildDevices()?.find { it.deviceNetworkId == "${device.deviceNetworkId}-${channelID}"}
			if (currentchild == null) {
				log.debug "Creating child for ch${i}"
                log.debug "${device.deviceNetworkId}-${channelID}"
				addChildDevice("Control4 Outlet Switch Channel", "${device.deviceNetworkId}-${channelID}", null, [completedSetup: true, label: "${device.displayName} CH${i}"])
			}
		} catch (e) {
			log.debug "Error adding child ${i}: ${e}"
		}
	}
}
private removeChildren() {
	log.debug "Removing Child Devices"
	try {
		getChildDevices()?.each {
			try {
				deleteChildDevice(it.deviceNetworkId)
			} catch (e) {
				log.debug "Error deleting ${it.deviceNetworkId}, probably locked into a SmartApp: ${e}"
			}
		}
	} catch (err) {
		log.debug "Either no children exist or error finding child devices for some reason: ${err}"
	}
}
def uninstalled() {

	log.debug "uninstalled()"
	response("zcl rftd")

}

private getEndpointId() {
	//log.debug "Device.endpoint is $device.endpointId"
	new BigInteger(device.endpointId, 16).toString()
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}


private String swapEndianHex(String hex) {
	reverseArray(hex.decodeHex()).encodeHex()
}

private byte[] reverseArray(byte[] array) {
	int i = 0;
	int j = array.length - 1;
	byte tmp;
	while (j > i) {
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}
	return array
}

def parseDescriptionAsMap(description) {
	(description - "read attr - ").split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}