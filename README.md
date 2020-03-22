<img src="https://raw.githubusercontent.com/ayedo/tcpkiller/master/src/main/deploy/package/linux/TcpKiller.png?token=ABEFFFQDQAVYXRAQF3JGPGS6QDESA" width="150" height="150" alt="logo">

# TcpKiller

A GUI application to kill a process which is listening on a tcp port.

Main Features:
 - Shows a list of processes, and tcp ports they are listening to
 - The list can by filtered by port number
 - You can select a process, and terminate it
 
There are many tools that implement this functionality. This tool will additionally:

 - Resolve the application name of java processes (if `jps` is available)
 - For IANA registered ports you can hover over the port number, and it will show additional information
 - On windows, will show the names of services running as children of `svchost.exe`

# Demonstration

![Basic Demonstration of TcpKiller](screencaptures/basic-demonstration.gif&s=200)

# Installation

Download the installer for your platform from [the releases page](https://github.com/ayedo/tcpkiller/releases).

# Requirements

The requirements depend on the operation system used.

## OS X

The following command line tools are required:

 - ps
 - kill
 - lsof
 
 Successfully tested on: 10.14.6 (18G2022)
 
## Windows

The following command line tools are required:

 - tasklist
 - taskkill
 - netstat

Successfully tested on: Windows 10

## Caveats

On Windows some services are started by the operation system under the system process which cannot be terminated by TcpKiller.

TcpKiller may still help you find the culprit:
1. Hover over the port number. If it's a registered IANA port, it will tell you it's name which could help you find out which service is listening on the port.
2. If it's a non IANA registered port, it could be `http.sys`, which is the windows integrated http server infrastructure. Run `netsh http show servicestate`. If there is a process listening through `http.sys`, you should be able to see it under 'request queue'.

# Attribution

I would like to thank [Freepik](https://www.flaticon.com/free-icon/star_1747901) for providing the logo for this project.
