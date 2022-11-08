package com.mainsteam.stm.ct.bo;



/**
 * @author SaltsTan
 *
 */
public class MsAverageWeb {


    private static final long serialVersionUID = 1L;
    
    private Integer id;
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

    private String resource_id;
    private String create_time;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
