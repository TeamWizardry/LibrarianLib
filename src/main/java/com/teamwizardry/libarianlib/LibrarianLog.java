package com.teamwizardry.libarianlib;

public class LibrarianLog extends LoggerBase {
	public static final LibrarianLog I = new LibrarianLog();
	
	protected LibrarianLog() {
		super("LibrarianLib");
	}
	
	
	@Override
	public LoggerBase getInstance() {
		return I;
	}

}
