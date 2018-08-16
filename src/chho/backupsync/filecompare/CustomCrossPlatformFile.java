package chho.backupsync.filecompare;

import java.io.File;
import java.net.URI;

class CustomCrossPlatformFile extends File {
	private static final long serialVersionUID = -2343748062649203898L;

	public CustomCrossPlatformFile(String pathname) {
		super(pathname);
	}

	public CustomCrossPlatformFile(URI uri) {
		super(uri);
	}

	public CustomCrossPlatformFile(String parent, String child) {
		super(parent, child);
	}

	public CustomCrossPlatformFile(File parent, String child) {
		super(parent, child);
	}

	@Override
	public int compareTo(File pathname) {
		return this.getName().compareTo(pathname.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.isDirectory() != ((CustomCrossPlatformFile)obj).isDirectory())
			return false;
		
		return (this.getName() == ((CustomCrossPlatformFile)obj).getName()) ? true : false;
	}
}
