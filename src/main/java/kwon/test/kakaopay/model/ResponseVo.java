package kwon.test.kakaopay.model;


public class ResponseVo extends RootVo {
	
	private boolean success;
	private int ecode; // error code
	private Object data; // responsedata
	private PageVo page;
//	private Map<String, Object> ext; // for extra data
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getEcode() {
		return ecode;
	}

	public void setEcode(int ecode) {
		this.ecode = ecode;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public PageVo getPage() {
		return page;
	}
	public void setPage(PageVo page) {
		this.page = page;
	}
	
//	public Map<String, Object> getExt() {
//		return ext;
//	}
//	public void setExt(Map<String, Object> ext) {
//		this.ext = ext;
//	}
//	
//	@JsonIgnore
//	public void addExtraData(String key, Object val) {
//		if(this.ext == null) {
//			this.ext = new HashMap();
//		}
//		
//		this.ext.put(key, val);
//	}
}
