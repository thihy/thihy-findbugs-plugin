package com.thihy.findbugs.log;

import org.apache.bcel.classfile.Field;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class NonFinalLogFieldDetector extends OpcodeStackDetector {

	private final BugReporter bugReporter;

	public NonFinalLogFieldDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void visitField(Field obj) {
		super.visitField(obj);
		XField f = XFactory.createXField(this);
		if (f.getSignature().equals("Lorg/slf4j/Logger;")) {
			if (!f.isFinal()) {
				bugReporter.reportBug(new BugInstance(this,
						"THY_NON_FINAL_LOGGER_FIELD", NORMAL_PRIORITY).addClass(
						this).addField(f));
			}
		}
	}

	@Override
	public void sawOpcode(int seen) {
	}
}
