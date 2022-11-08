<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.text.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
String id = sf.format(new Date());
%>
<script type="text/javascript" src="<%=basePath%>resource/module/topo/util/canvas.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/module/topo/graph/layout.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/module/topo/contextMenu/TopoMenu.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/module/topo/util/Tools.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/module/topo/graph/topo.js"></script>
<script type="text/javascript">
	function AlarmTopo(){
		this.loadData=function(data){
			var ctx = this;
			var id = "<%=id%>";
			if(data){
				data.cfg.holder.css({
					width:data.cfg.width||400,
					height:data.cfg.height||400
				});
				this.topo= new Topo({
					holder:$(data.cfg.holder).get(0),
					mode:"alarm"
				});
				this.topo.ignoreLoadCfg=true;
				//预处理下链路
				for(var i=0;i<data.links.length;++i){
					var item = data.links[i];
					item.fromType="node";
					item.toType="node";
					item.id=i;
				}
				//预处理节点
				for(var i=0;i<data.nodes.length;++i){
					var item = data.nodes[i];
					item.x=0;
					item.y=0;
					item.visible=true;
				}
				data.groups=[];
				data.others=[];
				this.topo.draw(data);
				if(data.cfg){
					this.topo.setCfg(data.cfg);
				}
				this.layout = new Layout({
					width:this.topo.width,
					height:this.topo.height,
					data:this.topo.els,
					paper:this.topo.paper,
					holder:this.topo.holder
				});
				//布局
				var ref = null;
				for(var o in this.topo.els){
					ref=this.topo.els[o];
					break;
				}
				this.layout.star({r:150,refEl:ref});
				//data.cfg.holder.find("tspan").attr("dy","1");
				this.topo.setEditable(false);
			}
		};
	};
	var alarmTopo = new AlarmTopo();
</script>

