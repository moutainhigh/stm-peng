package com.mainsteam.stm.portal.report.customchart;

public class UniqueCategoryLabel implements Comparable<UniqueCategoryLabel> {

    private Integer id;
    private String value;

    public UniqueCategoryLabel(Integer id, String value) {
        this.value = value;
        this.id = id;
    }

    @Override
    public int compareTo(UniqueCategoryLabel v) {
        return this.id.compareTo(v.id);
    }

    @Override
    public boolean equals(Object v) {
        return v instanceof UniqueCategoryLabel && this.id.equals(((UniqueCategoryLabel) v).id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
    
}
