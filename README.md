FlightPlot
==========

[![Build Status](https://travis-ci.org/PX4/FlightPlot.svg?branch=master)](https://travis-ci.org/PX4/FlightPlot)

Universal flight log plotter

Releases can be found on [GitHub releases](https://github.com/PX4/FlightPlot/releases).

Overview
--------

### Supported formats:
 - PX4 log (.px4log, .bin)
 - APM log (.bin)
 - ULog (.ulg)

### Features:
 - Data processing: low pass filtering, scaling, shifting, derivative, integral, etc.
 - Track export in KML and GPS format
 - Saving plot as image


Building from source
--------------------

Requirements:
 -  Java 6 or newer (JDK, http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 -  ant

Clone the repository. The `--recursive` flag is required to pull in the [jMAVlib](https://github.com/PX4/jMAVlib) submodule).
```
git clone --recursive https://github.com/PX4/FlightPlot.git
```

Build:
```
cd FlightPlot
ant
```

If you want to create deb file for ubuntu, use gen_deb.
```
cd FlightPlot
ant gen_deb
sudo dpkg -i out/production/FlightPlot.deb
```

Run:
```
java -jar out/production/flightplot.jar
```
---
### 以下是魔改版FlightPlot的使用方式

**使用方法：**
- 先打开需要分析的ulg日志
- 在Fields List中添加如下两个解析器
	- vehicle_global_position_0.lon
	- vehicle_global_position_0.lat
- 在Menu选择地图模式（如下图所示）
- 点击Panel中的数据，可以在地图中新增锚点（如下图所示）

![图示一](https://imgs.wiki/imgs/2023/07/11/acdb9b5cbd2de76e.png)

![图示二](https://imgs.wiki/imgs/2023/07/11/61dc97fe10a653e9.png)
