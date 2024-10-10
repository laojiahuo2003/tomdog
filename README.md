Tomdog是简化版的tomcat，目的是学习tomcat的思想
- 本项目的connectors只有一个connector，不支持Https
- 只能挂载到/的一个context，不支持多个Host域名和多个Context
我们专注于实现一个仅支持HTTP、仅支持一个Web App的Web服务器，把HTTPS、HTTP/2、HTTP/3、Host、Cluster（集群）等功能全部扔给Nginx即可。
