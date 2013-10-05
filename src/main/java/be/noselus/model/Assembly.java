package be.noselus.model;

public class Assembly {
	
	public enum Level {
		DEPUTY_CHAMBER,
		SENATE,
		REGION,
		LOCAL,
		PROVINCE,
		EUROPE
	}
	
	private String label;
	private Level level; 
	
	public Assembly(String label, Level level) {
		this.label = label;
		this.level = level;
	}

	public String getLabel() {
		return label;
	}

	public Level getLevel() {
		return level;
	}
	
	

}
