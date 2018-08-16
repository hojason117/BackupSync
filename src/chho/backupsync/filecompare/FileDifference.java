package chho.backupsync.filecompare;

class FileDifference {
	static enum DiffType {
		NOTINSOURCE("file not found in source"),
		NOTINDEST("file not found in destination"),
		SIZE("size"),
		CREATIONTIME("creation time"),
		LASTMODIFIEDTIME("last modified time");
		
		String typeName;
		
		DiffType(String name) {
			typeName = name;
		}
	}
	
	FileDifference(DiffType type, String sourceAttri, String destAttri) {
		diffType = type;
		this.sourceAttri = sourceAttri;
		this.destAttri = destAttri;
	}
	
	DiffType diffType;
	String sourceAttri;
	String destAttri;
}
