package com.mainsteam.stm.camera.web.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CustomGroupVo
  implements Serializable
{
  private static final long serialVersionUID = -7801805987064486717L;
  private Long id;
  private String name;
  private String description;
  private String groupType;
  private Long entryId;
  private Date entryDatetime;
  private List<String> resourceInstanceIds;
  private List<CustomGroupVo> childCustomGroupVo;
  private Long pid;
  
  public Long getId()
  {
    return id;
  }
  
  public void setId(Long id) { this.id = id; }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) { this.name = name; }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) { this.description = description; }
  
  public Long getEntryId() {
    return entryId;
  }
  
  public void setEntryId(Long entryId) { this.entryId = entryId; }
  
  public Date getEntryDatetime() {
    return entryDatetime;
  }
  
  public void setEntryDatetime(Date entryDatetime) { this.entryDatetime = entryDatetime; }
  
  public List<String> getResourceInstanceIds() {
    return resourceInstanceIds;
  }
  
  public void setResourceInstanceIds(List<String> resourceInstanceIds) { this.resourceInstanceIds = resourceInstanceIds; }
  
  public String getGroupType() {
    return groupType;
  }
  
  public void setGroupType(String groupType) { this.groupType = groupType; }
  
  public Long getPid() {
    return pid;
  }
  
  public void setPid(Long pid) { this.pid = pid; }
  
  public List<CustomGroupVo> getChildCustomGroupVo() {
    return childCustomGroupVo;
  }
  
  public void setChildCustomGroupVo(List<CustomGroupVo> childCustomGroupVo) { this.childCustomGroupVo = childCustomGroupVo; }
}

