package com.magic.api.commons.atlas.utils;

import java.io.PrintStream;
import java.io.PrintWriter;

public class BaseAppException extends Exception {

	private static final long serialVersionUID = 3085691578868990069L;

	/**
	 * 嵌入的异常实例
	 */
	protected Throwable cause;

	/**
	 * 构造函数
	 */
	public BaseAppException() {
		super("Error occurred in application.");
	}

	/**
	 * 构造函数
	 * 
	 * @param message
	 *            异常消息
	 */
	public BaseAppException(String message) {
		super(message);
	}

	/**
	 * 构造函数
	 * 
	 * @param message
	 *            异常消息
	 * @param cause
	 *            嵌入的异常实例
	 */
	public BaseAppException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}
	
	public BaseAppException(Throwable cause) {
		super(cause.getMessage());
		setStackTrace(cause.getStackTrace());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#initCause(java.lang.Throwable)
	 */
	public Throwable initCause(Throwable cause) {
		this.cause = cause;
		return cause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		// Get this exception's message.
		String msg = super.getMessage();
		Throwable parent = this;
		Throwable child;
		// Look for nested exceptions.
		while ((child = getNestedException(parent)) != null) {
			// Get the child's message.
			String msg2 = child.getMessage();
			// If we found a message for the child exception,
			// we append it.
			if (msg2 != null) {
				if (msg != null) {
					msg += ": " + msg2;
				} else {
					msg = msg2;
				}
			}
			// Any nested ApplicationException will append its own
			// children, so we need to break out of here.
			if (child instanceof BaseAppException) {
				break;
			}
			parent = child;
		}
		// Return the completed message.
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		// Print the stack trace for this exception.
		super.printStackTrace();
		Throwable parent = this;
		Throwable child;
		// Print the stack trace for each nested exception.
		while ((child = getNestedException(parent)) != null) {
			if (child != null) {
				System.err.print("Caused by: ");
				child.printStackTrace();
				if (child instanceof BaseAppException) {
					break;
				}
				parent = child;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {
		// Print the stack trace for this exception.
		super.printStackTrace(s);
		Throwable parent = this;
		Throwable child;
		// Print the stack trace for each nested exception.
		while ((child = getNestedException(parent)) != null) {
			if (child != null) {
				s.print("Caused by: ");
				child.printStackTrace(s);
				if (child instanceof BaseAppException) {
					break;
				}
				parent = child;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter w) {
		// Print the stack trace for this exception.
		super.printStackTrace(w);
		Throwable parent = this;
		Throwable child;
		// Print the stack trace for each nested exception.
		while ((child = getNestedException(parent)) != null) {
			if (child != null) {
				w.print("Caused by: ");
				child.printStackTrace(w);
				if (child instanceof BaseAppException) {
					break;
				}
				parent = child;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getCause()
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * 获取被嵌入的异常
	 * 
	 * @param e
	 *            包裹异常
	 * @return 嵌入异常
	 */
	public Throwable getNestedException(Throwable e) {
		if (e instanceof BaseAppException)
			if (e != null) {
				return e.getCause();
			}
		return null;
	}

}
