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
	definition (name: "Control4 Outlet Dimmer Channel", namespace: "lights0123", author: "Ben Schattinger") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
	}

	preferences {
		input "OnSpeed", "number", title: "Turn On Speed", description: "The speed in milliseconds in which the dimmer turns on", range: "0..9910", defaultValue: 1500, required: true, displayDuringSetup: true
		input "OffSpeed", "number", title: "Turn Off Speed", description: "The speed in milliseconds in which the dimmer turns off", range: "0..9900", defaultValue: 1500, required: true, displayDuringSetup: true
		input "DefaultOnValue", "number", title: "Default On Value", description: "The default value you want ST to turn on to, in case the last dimmed value is lost", range: "0..100", defaultValue: 100, required: true, displayDuringSetup: true
	}
    
	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "status", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
			}
			tileAttribute("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action: "switch level.setLevel"
			}
		}

		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 2, width: 4, inactiveLabel: false) {
			state "level", action: "switch level.setLevel"
		}
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main(["switch"])
		details(["status", "switch", "levelSliderControl", "refresh"])
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
	parent.childOn(device.deviceNetworkId, DefaultOnValue ?: 100, OnSpeed ?: 1500)
}

def off() {
	log.debug "Executing 'off'"
	parent.childOff(device.deviceNetworkId, OffSpeed ?: 1500)
}

def setLevel(Integer level) {
	log.debug "Executing 'setLevel'"
	parent.childSetLevel(device.deviceNetworkId, level)
}

def setState(String key, value) {
	state[key] = value
}