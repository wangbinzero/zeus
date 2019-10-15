# 数字货币交易所实时行情 Springboot + Netty

> 目前支持货币数据解析 后续会逐步添加解析

- websocket连接火币服务器
- K线作为历史数据写入mongoDB
- 获取okex最新USD汇率写入redis
- 支持http查询历史K线数据
- 支持配置文件订阅交易对
- 支持其它交易所扩展
- 支持websocket本地连接
- 自定义协议


## Demo
![image](https://github.com/wangbinzero/zeus/blob/master/image/deal.png)

![image](https://github.com/wangbinzero/zeus/blob/master/image/depth.png)

![image](https://github.com/wangbinzero/zeus/blob/master/image/kline.png)

![image](https://github.com/wangbinzero/zeus/blob/master/image/http_kline.png)


