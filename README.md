# lights0123/SmartThings

A collection of various SmartThings device types.

## Control4
I've continued to reverse engineer several Control4 devices, based on the work of [pstuart](https://github.com/pstuart/smartthings-ps/blob/master/devicetypes/Control4%20Zigbee%20HA%20Dimmer.groovy). Specifically, these are the [LOZ-5S1-W](https://content.abt.com/documents/35346/loz5s1w_ins.pdf) and the [LOZ-5D1-W](https://content.abt.com/documents/35347/loz5d1w_ins.pdf).

### Installation
Install the device handlers like you normally would through the IDE. You need both the regular outlet file, as well as the Channel For example, if you're using a dimmer, you need to install both `control4-outlet-dimmer` and `control4-outlet-dimmer-channel.groovy`. If you're using a dimmer and a switch, install all 4 files. Upon installation, two channels should automatically be created and will show up in the Things list. Otherwise, you may need to open the settings menu and hit save.
