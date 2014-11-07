package com.thihy.findbugs.log;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class MisuseLogNameDetector extends OpcodeStackDetector {
	private BugReporter bugReporter;

	public MisuseLogNameDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void sawOpcode(int seen) {
		for (LoggerGetMethod method : LOGGER_GET_METHODS) {
			if (seen == method.seen
					&& getClassConstantOperand().equals(method.className)
					&& getNameConstantOperand().equals(method.methodName)
					&& getSigConstantOperand().equals(method.sig)) {
				OpcodeStack.Item top = stack.getStackItem(0);
				Object value = top.getConstant();
				String paramClassName = value instanceof Class ? ((Class) value)
						.getName() : value.toString();
				String expectedClassName = getClassName();
				boolean ok = expectedClassName.equals(paramClassName);
				if (!ok) {
					bugReporter.reportBug(new BugInstance(this,
							"THY_MISUSE_LOG_NAME_BUG", NORMAL_PRIORITY)
							.addClassAndMethod(this)
							.addString(
									"Expected class [" + expectedClassName
											+ "], but is [" + paramClassName
											+ "]").addSourceLine(this));
				}
			}
		}
	}

	private static final List<LoggerGetMethod> LOGGER_GET_METHODS = new ArrayList<LoggerGetMethod>();
	static {
		LOGGER_GET_METHODS.add(new LoggerGetMethod(INVOKESTATIC,
				"org/slf4j/LoggerFactory", "getLogger",
				"(Ljava/lang/Class;)Lorg/slf4j/Logger;"));
	}

	private static class LoggerGetMethod {
		public final int seen;
		public final String className;
		public final String methodName;
		public final String sig;

		public LoggerGetMethod(int seen, String className, String methodName,
				String sig) {
			super();
			this.seen = seen;
			this.className = className;
			this.methodName = methodName;
			this.sig = sig;
		}

	}
}
