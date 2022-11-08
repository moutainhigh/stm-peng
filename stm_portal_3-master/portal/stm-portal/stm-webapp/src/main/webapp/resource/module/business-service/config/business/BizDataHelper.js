/**
 * 数据提供者，负责和后台服务器交互
 */
function BizDataHelper(args){
	
};
BizDataHelper.prototype = {
	/**
	 * 获取业务图数据
	 * @param id 业务拓扑的id
	 */
	getBusiness:function(id){
		return {"nodes":[{"attr":{"x":358,"y":122,"w":50,"h":60,"src":"img/business/10_dbserver.png"},"instanceId":123,"type":"net","ip":"192.1.1.1","id":1},{"attr":{"x":257,"y":284,"w":50,"h":60,"src":"img/business/10_router.png"},"instanceId":321,"type":"net","ip":"234.10.122.13","id":2},{"attr":{"x":83,"y":43,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"10.155.12.165","id":3},{"attr":{"x":590.5234375,"y":64,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"10.155.12.118","id":4},{"attr":{"x":303,"y":445,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"101.234.12.12","id":5},{"attr":{"x":77.078125,"y":362,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"118.114.115.110","id":6},{"attr":{"x":532.0390625,"y":374,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"192.168.117.115","id":7},{"attr":{"x":802.5234375,"y":382,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"10.11.12.13","id":8},{"attr":{"x":785,"y":189,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"233.125.2.56","id":9},{"attr":{"x":1028.5234375,"y":35,"w":50,"h":60,"src":"img/business/10_switch2.png"},"instanceId":258,"type":"net","ip":"165.167.114.123","id":10}],"links":[{"id":1,"from":1,"to":2,"instanceId":345,"attr":{}},{"id":2,"from":1,"to":3,"instanceId":345,"attr":{}},{"id":3,"from":1,"to":4,"instanceId":345,"attr":{}},{"id":4,"from":2,"to":5,"instanceId":345,"attr":{}},{"id":5,"from":2,"to":6,"instanceId":345,"attr":{}},{"id":6,"from":5,"to":7,"instanceId":345,"attr":{}},{"id":7,"from":7,"to":1,"instanceId":345,"attr":{}},{"id":9,"from":7,"to":8,"instanceId":345,"attr":{}},{"id":10,"from":4,"to":9,"instanceId":345,"attr":{}},{"id":11,"from":9,"to":10,"instanceId":345,"attr":{}},{"id":12,"from":1,"to":3,"instanceId":345,"fromDirection":"l","toDirection":"r"}]};
		/*return {
			nodes:[{
				attr:{
					x:123,y:321,w:50,h:60,src:"img/business/10_dbserver.png"
				},
				instanceId:123,type:"net",ip:"192.1.1.1",
				id:1
			},{
				attr:{
					x:456,y:364,w:50,h:60,src:"img/business/10_router.png"
				},
				instanceId:321,type:"net",ip:"234.10.122.13",
				id:2
			},{
				attr:{
					x:547,y:10,w:50,h:60,src:"img/business/10_switch2.png"
				},
				instanceId:258,type:"net",ip:"10.155.12.165",
				id:3
			}],
			links:[{
				id:1,from:1,to:2,instanceId:345,attr:{}
			},{
				id:2,from:1,to:3,instanceId:345,attr:{},type:"poly",attr:{direction:"rl"}
			},{
				id:3,from:1,to:2,instanceId:345,attr:{},type:"poly",attr:{direction:"rl"}
			}]
		};*/
	}
};