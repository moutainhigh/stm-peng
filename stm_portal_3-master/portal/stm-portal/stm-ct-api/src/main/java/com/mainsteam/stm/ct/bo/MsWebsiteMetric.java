package com.mainsteam.stm.ct.bo;


public class MsWebsiteMetric {

    private static final long serialVersionUID = 1L;


    private String id;
    //首字节
    private Integer first_char;
    //DOM Ready
    private Integer dom_ready;
    //页面完全加载
    private Integer page_ready;
    // DNS查询
    private Integer dns_select;
    //TCP连接
    private Integer tcp_collect;
    //请求响应
    private Integer req_answer;
    //内容传输
    private Integer send_content;
    //DOM解析
    private Integer dom_parse;
    //资源加载
    private Integer resource_load;

    //网址
    private String name;
    //进入方式 navigate导航
    private String type;
    //发起方式 navigation
    private String initiator_type;
    //进入方式 navigation
    private String entry_type;
    //重定向次数
    private Integer redirect_count;
    //传递大小
    private Integer transfer_size;
    //编码主体大小
    private Integer encoded_body_size;
    //持续的时间
    private Integer duration;

    private Integer start_time;
    private Integer worker_start;
    private String server_timing;

    private String resource_id;
    private String create_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getFirst_char() {
		return first_char;
	}
	public void setFirst_char(Integer first_char) {
		this.first_char = first_char;
	}
	public Integer getDom_ready() {
		return dom_ready;
	}
	public void setDom_ready(Integer dom_ready) {
		this.dom_ready = dom_ready;
	}
	public Integer getPage_ready() {
		return page_ready;
	}
	public void setPage_ready(Integer page_ready) {
		this.page_ready = page_ready;
	}
	public Integer getDns_select() {
		return dns_select;
	}
	public void setDns_select(Integer dns_select) {
		this.dns_select = dns_select;
	}
	public Integer getTcp_collect() {
		return tcp_collect;
	}
	public void setTcp_collect(Integer tcp_collect) {
		this.tcp_collect = tcp_collect;
	}
	public Integer getReq_answer() {
		return req_answer;
	}
	public void setReq_answer(Integer req_answer) {
		this.req_answer = req_answer;
	}
	public Integer getSend_content() {
		return send_content;
	}
	public void setSend_content(Integer send_content) {
		this.send_content = send_content;
	}
	public Integer getDom_parse() {
		return dom_parse;
	}
	public void setDom_parse(Integer dom_parse) {
		this.dom_parse = dom_parse;
	}
	public Integer getResource_load() {
		return resource_load;
	}
	public void setResource_load(Integer resource_load) {
		this.resource_load = resource_load;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInitiator_type() {
		return initiator_type;
	}
	public void setInitiator_type(String initiator_type) {
		this.initiator_type = initiator_type;
	}
	public String getEntry_type() {
		return entry_type;
	}
	public void setEntry_type(String entry_type) {
		this.entry_type = entry_type;
	}
	public Integer getRedirect_count() {
		return redirect_count;
	}
	public void setRedirect_count(Integer redirect_count) {
		this.redirect_count = redirect_count;
	}
	public Integer getTransfer_size() {
		return transfer_size;
	}
	public void setTransfer_size(Integer transfer_size) {
		this.transfer_size = transfer_size;
	}
	public Integer getEncoded_body_size() {
		return encoded_body_size;
	}
	public void setEncoded_body_size(Integer encoded_body_size) {
		this.encoded_body_size = encoded_body_size;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getStart_time() {
		return start_time;
	}
	public void setStart_time(Integer start_time) {
		this.start_time = start_time;
	}
	public Integer getWorker_start() {
		return worker_start;
	}
	public void setWorker_start(Integer worker_start) {
		this.worker_start = worker_start;
	}
	public String getServer_timing() {
		return server_timing;
	}
	public void setServer_timing(String server_timing) {
		this.server_timing = server_timing;
	}
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
    
}
