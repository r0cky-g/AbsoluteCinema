package ca.yorku.eecs4314group12.movie.dto;

public class TmdbCreditsCrewDTO {
	
	private String original_name;
	private String department;
	private String job;
	private String profile_path;
	
	public String getOriginal_name() {
		return original_name;
	}
	
	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}
	
	public String getDepartment() {
		return department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getJob() {
		return job;
	}
	
	public void setJob(String job) {
		this.job = job;
	}

	public String getProfile_path() {
		return profile_path;
	}

	public void setProfile_path(String profile_path) {
		this.profile_path = profile_path;
	}
}
