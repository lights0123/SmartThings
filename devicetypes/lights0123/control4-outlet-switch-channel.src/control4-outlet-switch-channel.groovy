/**
 *  Control4 Outlet Dimmer Channel
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
	definition (name: "Control4 Outlet Switch Channel", namespace: "lights0123", author: "Ben Schattinger") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Switch"
	}

	preferences {
	}
    
	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "status", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
			}
		}

		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
		}
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main(["switch"])
		details(["status", "switch", "refresh"])
	}
}

def installed() {
	log.debug "installed()"
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	// TODO: handle 'level' attribute

}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	refresh()
}

def refresh() {
	log.debug "Executing 'refresh'"
	//parent.refresh()
}

def on() {
	log.debug "Executing 'on'"
	parent.childOn(device.deviceNetworkId)
}

def off() {
	log.debug "Executing 'off'"
	parent.childOff(device.deviceNetworkId)
}