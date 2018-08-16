package chho.backupsync.filecompare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class FileComparator {
	private final File sourcePath;
	private final File destPath;
	private ArrayList<DiffFile> diffFiles;
	
	FileComparator(String sourcePath, String destPath) {
		this.sourcePath = new File(sourcePath);
		this.destPath = new File(destPath);
		diffFiles = new ArrayList<DiffFile>();
	}
	
	class DiffFile {
		String sourcePath;
		String destPath;
		ArrayList<FileDifference> diffs;
		
		DiffFile(String sourcePath, String destPath) {
			this.sourcePath = sourcePath;
			this.destPath = destPath;
			diffs = new ArrayList<FileDifference>();
		}
	}
	
	public ArrayList<DiffFile> compare() {
		compareSingleFolder(sourcePath, destPath);
		return diffFiles;
	}
	
	private static class FileNameComparator implements Comparator<File> {
		@Override
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
	}
	
	private void compareSingleFolder(File source, File dest) {
		ArrayList<File> sourceFilesList = new ArrayList<File>(Arrays.asList(source.listFiles()));
		ArrayList<File> destFilesList = new ArrayList<File>(Arrays.asList(dest.listFiles()));
		
		PriorityQueue<File> sourceFiles = new PriorityQueue<File>(1, new FileNameComparator());
		PriorityQueue<File> destFiles = new PriorityQueue<File>(1, new FileNameComparator());
		
		for(File f : sourceFilesList)
			sourceFiles.add(f);
		for(File f : destFilesList)
			destFiles.add(f);
		
		while(sourceFiles.size() > 0 || destFiles.size() > 0) {
			if(sourceFiles.size() == 0) {
				while(destFiles.size() > 0) {
					DiffFile diffFile = new DiffFile("", destFiles.poll().getAbsolutePath());
					diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINSOURCE, "", ""));
					diffFiles.add(diffFile);
				}
				break;
			}
			if(destFiles.size() == 0) {
				while(sourceFiles.size() > 0) {
					DiffFile diffFile = new DiffFile(sourceFiles.poll().getAbsolutePath(), "");
					diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINDEST, "", ""));
					diffFiles.add(diffFile);
				}
				break;
			}
			
			if(sourceFiles.peek().getName() == destFiles.peek().getName()) {
				File s = sourceFiles.poll();
				File d = destFiles.poll();
				
				if(s.isDirectory() && d.isDirectory())
					compareSingleFolder(s, d);
				else if(!s.isDirectory() && !d.isDirectory())
					compareSingleFile(s, d);
				else {
					if(s.isDirectory()) {
						DiffFile diffFile = new DiffFile("", d.getAbsolutePath());
						diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINSOURCE, "", ""));
						diffFiles.add(diffFile);
						
						addWholeFolder(s, FileDifference.DiffType.NOTINDEST);
					}
					else {
						DiffFile diffFile = new DiffFile(s.getAbsolutePath(), "");
						diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINDEST, "", ""));
						diffFiles.add(diffFile);
						
						addWholeFolder(d, FileDifference.DiffType.NOTINSOURCE);
					}
				}
			}
			else {
				if(sourceFiles.contains(destFiles.peek())) {
					
					
					
					
					
				}
				else if(destFiles.contains(sourceFiles.peek())) {
					
					
					
					
					
				}
				else {
					File s = sourceFiles.poll();
					File d = destFiles.poll();
					DiffFile diffFile;
					
					diffFile = new DiffFile(s.getAbsolutePath(), "");
					diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINDEST, "", ""));
					diffFiles.add(diffFile);
					
					diffFile = new DiffFile("", d.getAbsolutePath());
					diffFile.diffs.add(new FileDifference(FileDifference.DiffType.NOTINSOURCE, "", ""));
					diffFiles.add(diffFile);
				}
			}
		}
	}
	
	private void compareSingleFile(File source, File dest) {
		BasicFileAttributes sourceAttri;
		BasicFileAttributes destAttri;
		DiffFile diffFile = new DiffFile(source.getAbsolutePath(), dest.getAbsolutePath());
		
		try {
			sourceAttri = Files.readAttributes(source.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			destAttri = Files.readAttributes(dest.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		
		if(sourceAttri.size() != destAttri.size())
			diffFile.diffs.add(new FileDifference(FileDifference.DiffType.SIZE, String.valueOf(sourceAttri.size()), String.valueOf(destAttri.size())));
		if(sourceAttri.creationTime() != destAttri.creationTime())
			diffFile.diffs.add(new FileDifference(FileDifference.DiffType.CREATIONTIME, sourceAttri.creationTime().toString(), destAttri.creationTime().toString()));
		if(sourceAttri.lastModifiedTime() != destAttri.lastModifiedTime())
			diffFile.diffs.add(new FileDifference(FileDifference.DiffType.LASTMODIFIEDTIME, sourceAttri.lastModifiedTime().toString(), destAttri.lastModifiedTime().toString()));
		
		if(diffFile.diffs.size() > 0)
			diffFiles.add(diffFile);
	}
	
	private void addWholeFolder(File folder, FileDifference.DiffType type) {
		DiffFile diffFile;
		
		if(type == FileDifference.DiffType.NOTINSOURCE) {
			diffFile = new DiffFile("", folder.getAbsolutePath());
			diffFile.diffs.add(new FileDifference(type, "", ""));
			diffFiles.add(diffFile);
			
			ArrayList<File> toAdd = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			
			for(File f : toAdd) {
				diffFile = new DiffFile("", f.getAbsolutePath());
				diffFile.diffs.add(new FileDifference(type, "", ""));
				diffFiles.add(diffFile);
			}
		}
		else if(type == FileDifference.DiffType.NOTINDEST) {
			diffFile = new DiffFile(folder.getAbsolutePath(), "");
			diffFile.diffs.add(new FileDifference(type, "", ""));
			diffFiles.add(diffFile);
			
			ArrayList<File> toAdd = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			
			for(File f : toAdd) {
				diffFile = new DiffFile(f.getAbsolutePath(), "");
				diffFile.diffs.add(new FileDifference(type, "", ""));
				diffFiles.add(diffFile);
			}
		}
	}
}
