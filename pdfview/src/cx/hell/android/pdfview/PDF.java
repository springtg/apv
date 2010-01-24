package cx.hell.android.pdfview;

import java.io.File;
import java.io.FileDescriptor;


/**
 * Native PDF - interface to native code.
 * TODO: properly release resources
 */
public class PDF {
	static {
        System.loadLibrary("pdfview2");
	}
	
	/**
	 * Simple size class used in JNI to simplify parameter passing.
	 * This shouldn't be used anywhere outide of pdf-related code.
	 */
	public static class Size implements Cloneable {
		public int width;
		public int height;
		
		public Size() {
			this.width = 0;
			this.height = 0;
		}
		
		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public Size clone() {
			return new Size(this.width, this.height);
		}
	}
	
	/**
	 * Holds pointer to native pdf_t struct.
	 */
	@SuppressWarnings("unused")
	private int pdf_ptr = 0;

	/**
	 * Parse bytes as PDF file and store resulting pdf_t struct in pdf_ptr.
	 * @return error code
	 */
	private native int parseBytes(byte[] bytes);
	
	/**
	 * Parse PDF file.
	 * @param fileName pdf file name
	 * @return error code
	 */
	private native int parseFile(String fileName);
	
	/**
	 * Parse PDF file.
	 * @param fd opened file descriptor
	 * @return error code
	 */
	private native int parseFileDescriptor(FileDescriptor fd);

	/**
	 * Construct PDF structures from bytes stored in memory.
	 */
	public PDF(byte[] bytes) {
		this.parseBytes(bytes);
	}
	
	/**
	 * Construct PDF structures from file sitting on local filesystem.
	 */
	public PDF(File file) {
		this.parseFile(file.getAbsolutePath());
	}
	
	/**
	 * Construct PDF structures from opened file descriptor.
	 * @param file opened file descriptor
	 */
	public PDF(FileDescriptor file) {
		this.parseFileDescriptor(file);
	}
	
	/**
	 * Return page count from pdf_t struct.
	 */
	public native int getPageCount();
	
	/**
	 * Render a page.
	 * @param n page number, starting from 0
	 * @param zoom page size scalling
	 * @param left left edge
	 * @param right right edge
	 * @param passes requested size, used for size of resulting bitmap
	 * @return bytes of bitmap in Androids format
	 */
	public native int[] renderPage(int n, int zoom, int left, int top, PDF.Size rect);
	
	/**
	 * Get PDF page size, store it in size struct, return error code.
	 * @param n 0-based page number
	 * @param size size struct that holds result
	 * @return error code
	 */
	public native int getPageSize(int n, PDF.Size size);
	
	/**
	 * Free memory allocated in native code.
	 */
	private native void freeMemory();
	
	public void finalize() {
		this.freeMemory();
	}
}