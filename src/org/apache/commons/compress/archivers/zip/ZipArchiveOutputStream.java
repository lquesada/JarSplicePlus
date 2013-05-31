/*      */ package org.apache.commons.compress.archivers.zip;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.Deflater;
/*      */ import java.util.zip.ZipException;
/*      */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*      */ 
/*      */ public class ZipArchiveOutputStream extends ArchiveOutputStream
/*      */ {
/*      */   static final int BUFFER_SIZE = 512;
/*   79 */   protected boolean finished = false;
/*      */   private static final int DEFLATER_BLOCK_SIZE = 8192;
/*      */   public static final int DEFLATED = 8;
/*      */   public static final int DEFAULT_COMPRESSION = -1;
/*      */   public static final int STORED = 0;
/*      */   static final String DEFAULT_ENCODING = "UTF8";
/*      */ 
/*      */   @Deprecated
/*      */   public static final int EFS_FLAG = 2048;
/*      */   private CurrentEntry entry;
/*  126 */   private String comment = "";
/*      */ 
/*  131 */   private int level = -1;
/*      */ 
/*  137 */   private boolean hasCompressionLevelChanged = false;
/*      */ 
/*  142 */   private int method = 8;
/*      */ 
/*  148 */   private final List<ZipArchiveEntry> entries = new LinkedList();
/*      */ 
/*  153 */   private final CRC32 crc = new CRC32();
/*      */ 
/*  158 */   private long written = 0L;
/*      */ 
/*  163 */   private long cdOffset = 0L;
/*      */ 
/*  168 */   private long cdLength = 0L;
/*      */ 
/*  173 */   private static final byte[] ZERO = new byte[2];
/*      */ 
/*  178 */   private static final byte[] LZERO = new byte[4];
/*      */ 
/*  184 */   private final Map<ZipArchiveEntry, Long> offsets = new HashMap();
/*      */ 
/*  193 */   private String encoding = "UTF8";
/*      */ 
/*  202 */   private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
/*      */ 
/*  208 */   protected final Deflater def = new Deflater(this.level, true);
/*      */ 
/*  214 */   private final byte[] buf = new byte[512];
/*      */   private final RandomAccessFile raf;
/*      */   private final OutputStream out;
/*  227 */   private boolean useUTF8Flag = true;
/*      */ 
/*  232 */   private boolean fallbackToUTF8 = false;
/*      */ 
/*  237 */   private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
/*      */ 
/*  244 */   private boolean hasUsedZip64 = false;
/*      */ 
/*  246 */   private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
/*      */ 
/*  830 */   static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
/*      */ 
/*  834 */   static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
/*      */ 
/*  838 */   static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
/*      */ 
/*  842 */   static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
/*      */ 
/*  846 */   static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
/*      */ 
/*  850 */   static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
/*      */ 
/* 1187 */   private static final byte[] ONE = ZipLong.getBytes(1L);
/*      */ 
/*      */   public ZipArchiveOutputStream(OutputStream out)
/*      */   {
/*  253 */     this.out = out;
/*  254 */     this.raf = null;
/*      */   }
/*      */ 
/*      */   public ZipArchiveOutputStream(File file)
/*      */     throws IOException
/*      */   {
/*  264 */     OutputStream o = null;
/*  265 */     RandomAccessFile _raf = null;
/*      */     try {
/*  267 */       _raf = new RandomAccessFile(file, "rw");
/*  268 */       _raf.setLength(0L);
/*      */     } catch (IOException e) {
/*  270 */       if (_raf != null) {
/*      */         try {
/*  272 */           _raf.close();
/*      */         }
/*      */         catch (IOException localIOException1) {
/*      */         }
/*  276 */         _raf = null;
/*      */       }
/*  278 */       o = new FileOutputStream(file);
/*      */     }
/*  280 */     this.out = o;
/*  281 */     this.raf = _raf;
/*      */   }
/*      */ 
/*      */   public boolean isSeekable()
/*      */   {
/*  294 */     return this.raf != null;
/*      */   }
/*      */ 
/*      */   public void setEncoding(String encoding)
/*      */   {
/*  307 */     this.encoding = encoding;
/*  308 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  309 */     if ((this.useUTF8Flag) && (!ZipEncodingHelper.isUTF8(encoding)))
/*  310 */       this.useUTF8Flag = false;
/*      */   }
/*      */ 
/*      */   public String getEncoding()
/*      */   {
/*  320 */     return this.encoding;
/*      */   }
/*      */ 
/*      */   public void setUseLanguageEncodingFlag(boolean b)
/*      */   {
/*  330 */     this.useUTF8Flag = ((b) && (ZipEncodingHelper.isUTF8(this.encoding)));
/*      */   }
/*      */ 
/*      */   public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b)
/*      */   {
/*  339 */     this.createUnicodeExtraFields = b;
/*      */   }
/*      */ 
/*      */   public void setFallbackToUTF8(boolean b)
/*      */   {
/*  349 */     this.fallbackToUTF8 = b;
/*      */   }
/*      */ 
/*      */   public void setUseZip64(Zip64Mode mode)
/*      */   {
/*  397 */     this.zip64Mode = mode;
/*      */   }
/*      */ 
/*      */   public void finish()
/*      */     throws IOException
/*      */   {
/*  408 */     if (this.finished) {
/*  409 */       throw new IOException("This archive has already been finished");
/*      */     }
/*      */ 
/*  412 */     if (this.entry != null) {
/*  413 */       throw new IOException("This archives contains unclosed entries.");
/*      */     }
/*      */ 
/*  416 */     this.cdOffset = this.written;
/*  417 */     for (ZipArchiveEntry ze : this.entries) {
/*  418 */       writeCentralFileHeader(ze);
/*      */     }
/*  420 */     this.cdLength = (this.written - this.cdOffset);
/*  421 */     writeZip64CentralDirectory();
/*  422 */     writeCentralDirectoryEnd();
/*  423 */     this.offsets.clear();
/*  424 */     this.entries.clear();
/*  425 */     this.def.end();
/*  426 */     this.finished = true;
/*      */   }
/*      */ 
/*      */   public void closeArchiveEntry()
/*      */     throws IOException
/*      */   {
/*  438 */     if (this.finished) {
/*  439 */       throw new IOException("Stream has already been finished");
/*      */     }
/*      */ 
/*  442 */     if (this.entry == null) {
/*  443 */       throw new IOException("No current entry to close");
/*      */     }
/*      */ 
/*  446 */     if (!this.entry.hasWritten) {
/*  447 */       write(new byte[0], 0, 0);
/*      */     }
/*      */ 
/*  450 */     flushDeflater();
/*      */ 
/*  452 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  453 */     long bytesWritten = this.written - this.entry.dataStart;
/*  454 */     long realCrc = this.crc.getValue();
/*  455 */     this.crc.reset();
/*      */ 
/*  457 */     boolean actuallyNeedsZip64 = 
/*  458 */       handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
/*      */ 
/*  460 */     if (this.raf != null) {
/*  461 */       rewriteSizesAndCrc(actuallyNeedsZip64);
/*      */     }
/*      */ 
/*  464 */     writeDataDescriptor(this.entry.entry);
/*  465 */     this.entry = null;
/*      */   }
/*      */ 
/*      */   private void flushDeflater()
/*      */     throws IOException
/*      */   {
/*  472 */     if (this.entry.entry.getMethod() == 8) {
/*  473 */       this.def.finish();
/*  474 */       while (!this.def.finished())
/*  475 */         deflate();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode)
/*      */     throws ZipException
/*      */   {
/*  489 */     if (this.entry.entry.getMethod() == 8)
/*      */     {
/*  494 */       this.entry.entry.setSize(this.entry.bytesRead);
/*  495 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  496 */       this.entry.entry.setCrc(crc);
/*      */ 
/*  498 */       this.def.reset();
/*  499 */     } else if (this.raf == null) {
/*  500 */       if (this.entry.entry.getCrc() != crc) {
/*  501 */         throw new ZipException("bad CRC checksum for entry " + 
/*  502 */           this.entry.entry.getName() + ": " + 
/*  503 */           Long.toHexString(this.entry.entry.getCrc()) + 
/*  504 */           " instead of " + 
/*  505 */           Long.toHexString(crc));
/*      */       }
/*      */ 
/*  508 */       if (this.entry.entry.getSize() != bytesWritten)
/*  509 */         throw new ZipException("bad size for entry " + 
/*  510 */           this.entry.entry.getName() + ": " + 
/*  511 */           this.entry.entry.getSize() + 
/*  512 */           " instead of " + 
/*  513 */           bytesWritten);
/*      */     }
/*      */     else {
/*  516 */       this.entry.entry.setSize(bytesWritten);
/*  517 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  518 */       this.entry.entry.setCrc(crc);
/*      */     }
/*      */ 
/*  521 */     boolean actuallyNeedsZip64 = (effectiveMode == Zip64Mode.Always) || 
/*  522 */       (this.entry.entry.getSize() >= 4294967295L) || 
/*  523 */       (this.entry.entry.getCompressedSize() >= 4294967295L);
/*  524 */     if ((actuallyNeedsZip64) && (effectiveMode == Zip64Mode.Never)) {
/*  525 */       throw new Zip64RequiredException(
/*  526 */         Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
/*      */     }
/*  528 */     return actuallyNeedsZip64;
/*      */   }
/*      */ 
/*      */   private void rewriteSizesAndCrc(boolean actuallyNeedsZip64)
/*      */     throws IOException
/*      */   {
/*  538 */     long save = this.raf.getFilePointer();
/*      */ 
/*  540 */     this.raf.seek(this.entry.localDataStart);
/*  541 */     writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
/*  542 */     if ((!hasZip64Extra(this.entry.entry)) || (!actuallyNeedsZip64)) {
/*  543 */       writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
/*  544 */       writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
/*      */     } else {
/*  546 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*  547 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*      */     }
/*      */ 
/*  550 */     if (hasZip64Extra(this.entry.entry))
/*      */     {
/*  552 */       this.raf.seek(this.entry.localDataStart + 12L + 4L + 
/*  553 */         getName(this.entry.entry).limit() + 4L);
/*      */ 
/*  556 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
/*  557 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
/*      */ 
/*  559 */       if (!actuallyNeedsZip64)
/*      */       {
/*  562 */         this.raf.seek(this.entry.localDataStart - 10L);
/*  563 */         writeOut(ZipShort.getBytes(10));
/*      */ 
/*  567 */         this.entry.entry.removeExtraField(
/*  568 */           Zip64ExtendedInformationExtraField.HEADER_ID);
/*  569 */         this.entry.entry.setExtra();
/*      */ 
/*  573 */         if (this.entry.causedUseOfZip64) {
/*  574 */           this.hasUsedZip64 = false;
/*      */         }
/*      */       }
/*      */     }
/*  578 */     this.raf.seek(save);
/*      */   }
/*      */ 
/*      */   public void putArchiveEntry(ArchiveEntry archiveEntry)
/*      */     throws IOException
/*      */   {
/*  590 */     if (this.finished) {
/*  591 */       throw new IOException("Stream has already been finished");
/*      */     }
/*      */ 
/*  594 */     if (this.entry != null) {
/*  595 */       closeArchiveEntry();
/*      */     }
/*      */ 
/*  598 */     this.entry = new CurrentEntry((ZipArchiveEntry)archiveEntry, null);
/*  599 */     this.entries.add(this.entry.entry);
/*      */ 
/*  601 */     setDefaults(this.entry.entry);
/*      */ 
/*  603 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  604 */     validateSizeInformation(effectiveMode);
/*      */ 
/*  606 */     if (shouldAddZip64Extra(this.entry.entry, effectiveMode))
/*      */     {
/*  608 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(this.entry.entry);
/*      */ 
/*  612 */       ZipEightByteInteger size = ZipEightByteInteger.ZERO;
/*  613 */       if ((this.entry.entry.getMethod() == 0) && 
/*  614 */         (this.entry.entry.getSize() != -1L))
/*      */       {
/*  616 */         size = new ZipEightByteInteger(this.entry.entry.getSize());
/*      */       }
/*  618 */       z64.setSize(size);
/*  619 */       z64.setCompressedSize(size);
/*  620 */       this.entry.entry.setExtra();
/*      */     }
/*      */ 
/*  623 */     if ((this.entry.entry.getMethod() == 8) && (this.hasCompressionLevelChanged)) {
/*  624 */       this.def.setLevel(this.level);
/*  625 */       this.hasCompressionLevelChanged = false;
/*      */     }
/*  627 */     writeLocalFileHeader(this.entry.entry);
/*      */   }
/*      */ 
/*      */   private void setDefaults(ZipArchiveEntry entry)
/*      */   {
/*  635 */     if (entry.getMethod() == -1) {
/*  636 */       entry.setMethod(this.method);
/*      */     }
/*      */ 
/*  639 */     if (entry.getTime() == -1L)
/*  640 */       entry.setTime(System.currentTimeMillis());
/*      */   }
/*      */ 
/*      */   private void validateSizeInformation(Zip64Mode effectiveMode)
/*      */     throws ZipException
/*      */   {
/*  653 */     if ((this.entry.entry.getMethod() == 0) && (this.raf == null)) {
/*  654 */       if (this.entry.entry.getSize() == -1L) {
/*  655 */         throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
/*      */       }
/*      */ 
/*  659 */       if (this.entry.entry.getCrc() == -1L) {
/*  660 */         throw new ZipException("crc checksum is required for STORED method when not writing to a file");
/*      */       }
/*      */ 
/*  663 */       this.entry.entry.setCompressedSize(this.entry.entry.getSize());
/*      */     }
/*      */ 
/*  666 */     if (((this.entry.entry.getSize() >= 4294967295L) || 
/*  667 */       (this.entry.entry.getCompressedSize() >= 4294967295L)) && 
/*  668 */       (effectiveMode == Zip64Mode.Never))
/*  669 */       throw new Zip64RequiredException(
/*  670 */         Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
/*      */   }
/*      */ 
/*      */   private boolean shouldAddZip64Extra(ZipArchiveEntry entry, Zip64Mode mode)
/*      */   {
/*  693 */     return (mode == Zip64Mode.Always) || 
/*  690 */       (entry.getSize() >= 4294967295L) || 
/*  691 */       (entry.getCompressedSize() >= 4294967295L) || (
/*  692 */       (entry.getSize() == -1L) && 
/*  693 */       (this.raf != null) && (mode != Zip64Mode.Never));
/*      */   }
/*      */ 
/*      */   public void setComment(String comment)
/*      */   {
/*  701 */     this.comment = comment;
/*      */   }
/*      */ 
/*      */   public void setLevel(int level)
/*      */   {
/*  713 */     if ((level < -1) || 
/*  714 */       (level > 9)) {
/*  715 */       throw new IllegalArgumentException("Invalid compression level: " + 
/*  716 */         level);
/*      */     }
/*  718 */     this.hasCompressionLevelChanged = (this.level != level);
/*  719 */     this.level = level;
/*      */   }
/*      */ 
/*      */   public void setMethod(int method)
/*      */   {
/*  729 */     this.method = method;
/*      */   }
/*      */ 
/*      */   public boolean canWriteEntryData(ArchiveEntry ae)
/*      */   {
/*  741 */     if ((ae instanceof ZipArchiveEntry)) {
/*  742 */       return ZipUtil.canHandleEntryData((ZipArchiveEntry)ae);
/*      */     }
/*  744 */     return false;
/*      */   }
/*      */ 
/*      */   public void write(byte[] b, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  756 */     ZipUtil.checkRequestedFeatures(this.entry.entry);
/*  757 */     this.entry.hasWritten = true;
/*  758 */     if (this.entry.entry.getMethod() == 8) {
/*  759 */       writeDeflated(b, offset, length);
/*      */     } else {
/*  761 */       writeOut(b, offset, length);
/*  762 */       this.written += length;
/*      */     }
/*  764 */     this.crc.update(b, offset, length);
/*  765 */     count(length);
/*      */   }
/*      */ 
/*      */   private void writeDeflated(byte[] b, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  773 */     if ((length > 0) && (!this.def.finished())) {
/*  774 */       this.entry.bytesRead += length;
/*  775 */       if (length <= 8192) {
/*  776 */         this.def.setInput(b, offset, length);
/*  777 */         deflateUntilInputIsNeeded();
/*      */       } else {
/*  779 */         int fullblocks = length / 8192;
/*  780 */         for (int i = 0; i < fullblocks; i++) {
/*  781 */           this.def.setInput(b, offset + i * 8192, 
/*  782 */             8192);
/*  783 */           deflateUntilInputIsNeeded();
/*      */         }
/*  785 */         int done = fullblocks * 8192;
/*  786 */         if (done < length) {
/*  787 */           this.def.setInput(b, offset + done, length - done);
/*  788 */           deflateUntilInputIsNeeded();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  805 */     if (!this.finished) {
/*  806 */       finish();
/*      */     }
/*  808 */     destroy();
/*      */   }
/*      */ 
/*      */   public void flush()
/*      */     throws IOException
/*      */   {
/*  819 */     if (this.out != null)
/*  820 */       this.out.flush();
/*      */   }
/*      */ 
/*      */   protected final void deflate()
/*      */     throws IOException
/*      */   {
/*  857 */     int len = this.def.deflate(this.buf, 0, this.buf.length);
/*  858 */     if (len > 0) {
/*  859 */       writeOut(this.buf, 0, len);
/*  860 */       this.written += len;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeLocalFileHeader(ZipArchiveEntry ze)
/*      */     throws IOException
/*      */   {
/*  871 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/*  872 */     ByteBuffer name = getName(ze);
/*      */ 
/*  874 */     if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
/*  875 */       addUnicodeExtraFields(ze, encodable, name);
/*      */     }
/*      */ 
/*  878 */     this.offsets.put(ze, Long.valueOf(this.written));
/*      */ 
/*  880 */     writeOut(LFH_SIG);
/*  881 */     this.written += 4L;
/*      */ 
/*  884 */     int zipMethod = ze.getMethod();
/*      */ 
/*  886 */     writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, 
/*  887 */       (!encodable) && 
/*  888 */       (this.fallbackToUTF8), 
/*  889 */       hasZip64Extra(ze));
/*  890 */     this.written += 4L;
/*      */ 
/*  893 */     writeOut(ZipShort.getBytes(zipMethod));
/*  894 */     this.written += 2L;
/*      */ 
/*  897 */     writeOut(ZipUtil.toDosTime(ze.getTime()));
/*  898 */     this.written += 4L;
/*      */ 
/*  903 */     this.entry.localDataStart = this.written;
/*  904 */     if ((zipMethod == 8) || (this.raf != null)) {
/*  905 */       writeOut(LZERO);
/*  906 */       if (hasZip64Extra(this.entry.entry))
/*      */       {
/*  910 */         writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*  911 */         writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*      */       } else {
/*  913 */         writeOut(LZERO);
/*  914 */         writeOut(LZERO);
/*      */       }
/*      */     } else {
/*  917 */       writeOut(ZipLong.getBytes(ze.getCrc()));
/*  918 */       byte[] size = ZipLong.ZIP64_MAGIC.getBytes();
/*  919 */       if (!hasZip64Extra(ze)) {
/*  920 */         size = ZipLong.getBytes(ze.getSize());
/*      */       }
/*  922 */       writeOut(size);
/*  923 */       writeOut(size);
/*      */     }
/*      */ 
/*  926 */     this.written += 12L;
/*      */ 
/*  930 */     writeOut(ZipShort.getBytes(name.limit()));
/*  931 */     this.written += 2L;
/*      */ 
/*  934 */     byte[] extra = ze.getLocalFileDataExtra();
/*  935 */     writeOut(ZipShort.getBytes(extra.length));
/*  936 */     this.written += 2L;
/*      */ 
/*  939 */     writeOut(name.array(), name.arrayOffset(), name.limit());
/*  940 */     this.written += name.limit();
/*      */ 
/*  943 */     writeOut(extra);
/*  944 */     this.written += extra.length;
/*      */ 
/*  946 */     this.entry.dataStart = this.written;
/*      */   }
/*      */ 
/*      */   private void addUnicodeExtraFields(ZipArchiveEntry ze, boolean encodable, ByteBuffer name)
/*      */     throws IOException
/*      */   {
/*  957 */     if ((this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || 
/*  958 */       (!encodable)) {
/*  959 */       ze.addExtraField(new UnicodePathExtraField(ze.getName(), 
/*  960 */         name.array(), 
/*  961 */         name.arrayOffset(), 
/*  962 */         name.limit()));
/*      */     }
/*      */ 
/*  965 */     String comm = ze.getComment();
/*  966 */     if ((comm != null) && (!"".equals(comm)))
/*      */     {
/*  968 */       boolean commentEncodable = this.zipEncoding.canEncode(comm);
/*      */ 
/*  970 */       if ((this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || 
/*  971 */         (!commentEncodable)) {
/*  972 */         ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/*  973 */         ze.addExtraField(new UnicodeCommentExtraField(comm, 
/*  974 */           commentB.array(), 
/*  975 */           commentB.arrayOffset(), 
/*  976 */           commentB.limit()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeDataDescriptor(ZipArchiveEntry ze)
/*      */     throws IOException
/*      */   {
/*  988 */     if ((ze.getMethod() != 8) || (this.raf != null)) {
/*  989 */       return;
/*      */     }
/*  991 */     writeOut(DD_SIG);
/*  992 */     writeOut(ZipLong.getBytes(ze.getCrc()));
/*  993 */     int sizeFieldSize = 4;
/*  994 */     if (!hasZip64Extra(ze)) {
/*  995 */       writeOut(ZipLong.getBytes(ze.getCompressedSize()));
/*  996 */       writeOut(ZipLong.getBytes(ze.getSize()));
/*      */     } else {
/*  998 */       sizeFieldSize = 8;
/*  999 */       writeOut(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
/* 1000 */       writeOut(ZipEightByteInteger.getBytes(ze.getSize()));
/*      */     }
/* 1002 */     this.written += 8 + 2 * sizeFieldSize;
/*      */   }
/*      */ 
/*      */   protected void writeCentralFileHeader(ZipArchiveEntry ze)
/*      */     throws IOException
/*      */   {
/* 1014 */     writeOut(CFH_SIG);
/* 1015 */     this.written += 4L;
/*      */ 
/* 1017 */     long lfhOffset = ((Long)this.offsets.get(ze)).longValue();
/* 1018 */     boolean needsZip64Extra = (hasZip64Extra(ze)) || 
/* 1019 */       (ze.getCompressedSize() >= 4294967295L) || 
/* 1020 */       (ze.getSize() >= 4294967295L) || 
/* 1021 */       (lfhOffset >= 4294967295L);
/*      */ 
/* 1023 */     if ((needsZip64Extra) && (this.zip64Mode == Zip64Mode.Never))
/*      */     {
/* 1027 */       throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
/*      */     }
/*      */ 
/* 1031 */     handleZip64Extra(ze, lfhOffset, needsZip64Extra);
/*      */ 
/* 1035 */     writeOut(ZipShort.getBytes(ze.getPlatform() << 8 | (
/* 1036 */       !this.hasUsedZip64 ? 20 : 
/* 1037 */       45)));
/* 1038 */     this.written += 2L;
/*      */ 
/* 1040 */     int zipMethod = ze.getMethod();
/* 1041 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 1042 */     writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, 
/* 1043 */       (!encodable) && 
/* 1044 */       (this.fallbackToUTF8), 
/* 1045 */       needsZip64Extra);
/* 1046 */     this.written += 4L;
/*      */ 
/* 1049 */     writeOut(ZipShort.getBytes(zipMethod));
/* 1050 */     this.written += 2L;
/*      */ 
/* 1053 */     writeOut(ZipUtil.toDosTime(ze.getTime()));
/* 1054 */     this.written += 4L;
/*      */ 
/* 1059 */     writeOut(ZipLong.getBytes(ze.getCrc()));
/* 1060 */     if ((ze.getCompressedSize() >= 4294967295L) || 
/* 1061 */       (ze.getSize() >= 4294967295L)) {
/* 1062 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/* 1063 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*      */     } else {
/* 1065 */       writeOut(ZipLong.getBytes(ze.getCompressedSize()));
/* 1066 */       writeOut(ZipLong.getBytes(ze.getSize()));
/*      */     }
/*      */ 
/* 1069 */     this.written += 12L;
/*      */ 
/* 1072 */     ByteBuffer name = getName(ze);
/*      */ 
/* 1074 */     writeOut(ZipShort.getBytes(name.limit()));
/* 1075 */     this.written += 2L;
/*      */ 
/* 1078 */     byte[] extra = ze.getCentralDirectoryExtra();
/* 1079 */     writeOut(ZipShort.getBytes(extra.length));
/* 1080 */     this.written += 2L;
/*      */ 
/* 1083 */     String comm = ze.getComment();
/* 1084 */     if (comm == null) {
/* 1085 */       comm = "";
/*      */     }
/*      */ 
/* 1088 */     ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/*      */ 
/* 1090 */     writeOut(ZipShort.getBytes(commentB.limit()));
/* 1091 */     this.written += 2L;
/*      */ 
/* 1094 */     writeOut(ZERO);
/* 1095 */     this.written += 2L;
/*      */ 
/* 1098 */     writeOut(ZipShort.getBytes(ze.getInternalAttributes()));
/* 1099 */     this.written += 2L;
/*      */ 
/* 1102 */     writeOut(ZipLong.getBytes(ze.getExternalAttributes()));
/* 1103 */     this.written += 4L;
/*      */ 
/* 1106 */     writeOut(ZipLong.getBytes(Math.min(lfhOffset, 4294967295L)));
/* 1107 */     this.written += 4L;
/*      */ 
/* 1110 */     writeOut(name.array(), name.arrayOffset(), name.limit());
/* 1111 */     this.written += name.limit();
/*      */ 
/* 1114 */     writeOut(extra);
/* 1115 */     this.written += extra.length;
/*      */ 
/* 1118 */     writeOut(commentB.array(), commentB.arrayOffset(), commentB.limit());
/* 1119 */     this.written += commentB.limit();
/*      */   }
/*      */ 
/*      */   private void handleZip64Extra(ZipArchiveEntry ze, long lfhOffset, boolean needsZip64Extra)
/*      */   {
/* 1128 */     if (needsZip64Extra) {
/* 1129 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(ze);
/* 1130 */       if ((ze.getCompressedSize() >= 4294967295L) || 
/* 1131 */         (ze.getSize() >= 4294967295L)) {
/* 1132 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/* 1133 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*      */       }
/*      */       else {
/* 1136 */         z64.setCompressedSize(null);
/* 1137 */         z64.setSize(null);
/*      */       }
/* 1139 */       if (lfhOffset >= 4294967295L) {
/* 1140 */         z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
/*      */       }
/* 1142 */       ze.setExtra();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeCentralDirectoryEnd()
/*      */     throws IOException
/*      */   {
/* 1154 */     writeOut(EOCD_SIG);
/*      */ 
/* 1157 */     writeOut(ZERO);
/* 1158 */     writeOut(ZERO);
/*      */ 
/* 1161 */     int numberOfEntries = this.entries.size();
/* 1162 */     if ((numberOfEntries > 65535) && 
/* 1163 */       (this.zip64Mode == Zip64Mode.Never)) {
/* 1164 */       throw new Zip64RequiredException("archive contains more than 65535 entries.");
/*      */     }
/*      */ 
/* 1167 */     if ((this.cdOffset > 4294967295L) && (this.zip64Mode == Zip64Mode.Never)) {
/* 1168 */       throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
/*      */     }
/*      */ 
/* 1172 */     byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 
/* 1173 */       65535));
/* 1174 */     writeOut(num);
/* 1175 */     writeOut(num);
/*      */ 
/* 1178 */     writeOut(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
/* 1179 */     writeOut(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
/*      */ 
/* 1182 */     ByteBuffer data = this.zipEncoding.encode(this.comment);
/* 1183 */     writeOut(ZipShort.getBytes(data.limit()));
/* 1184 */     writeOut(data.array(), data.arrayOffset(), data.limit());
/*      */   }
/*      */ 
/*      */   protected void writeZip64CentralDirectory()
/*      */     throws IOException
/*      */   {
/* 1196 */     if (this.zip64Mode == Zip64Mode.Never) {
/* 1197 */       return;
/*      */     }
/*      */ 
/* 1200 */     if ((!this.hasUsedZip64) && (
/* 1201 */       (this.cdOffset >= 4294967295L) || (this.cdLength >= 4294967295L) || 
/* 1202 */       (this.entries.size() >= 65535)))
/*      */     {
/* 1204 */       this.hasUsedZip64 = true;
/*      */     }
/*      */ 
/* 1207 */     if (!this.hasUsedZip64) {
/* 1208 */       return;
/*      */     }
/*      */ 
/* 1211 */     long offset = this.written;
/*      */ 
/* 1213 */     writeOut(ZIP64_EOCD_SIG);
/*      */ 
/* 1216 */     writeOut(
/* 1217 */       ZipEightByteInteger.getBytes(44L));
/*      */ 
/* 1228 */     writeOut(ZipShort.getBytes(45));
/* 1229 */     writeOut(ZipShort.getBytes(45));
/*      */ 
/* 1232 */     writeOut(LZERO);
/* 1233 */     writeOut(LZERO);
/*      */ 
/* 1236 */     byte[] num = ZipEightByteInteger.getBytes(this.entries.size());
/* 1237 */     writeOut(num);
/* 1238 */     writeOut(num);
/*      */ 
/* 1241 */     writeOut(ZipEightByteInteger.getBytes(this.cdLength));
/* 1242 */     writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
/*      */ 
/* 1247 */     writeOut(ZIP64_EOCD_LOC_SIG);
/*      */ 
/* 1250 */     writeOut(LZERO);
/*      */ 
/* 1252 */     writeOut(ZipEightByteInteger.getBytes(offset));
/*      */ 
/* 1254 */     writeOut(ONE);
/*      */   }
/*      */ 
/*      */   protected final void writeOut(byte[] data)
/*      */     throws IOException
/*      */   {
/* 1263 */     writeOut(data, 0, data.length);
/*      */   }
/*      */ 
/*      */   protected final void writeOut(byte[] data, int offset, int length)
/*      */     throws IOException
/*      */   {
/* 1275 */     if (this.raf != null)
/* 1276 */       this.raf.write(data, offset, length);
/*      */     else
/* 1278 */       this.out.write(data, offset, length);
/*      */   }
/*      */ 
/*      */   private void deflateUntilInputIsNeeded() throws IOException
/*      */   {
/* 1283 */     while (!this.def.needsInput())
/* 1284 */       deflate();
/*      */   }
/*      */ 
/*      */   private void writeVersionNeededToExtractAndGeneralPurposeBits(int zipMethod, boolean utfFallback, boolean zip64)
/*      */     throws IOException
/*      */   {
/* 1297 */     int versionNeededToExtract = 10;
/* 1298 */     GeneralPurposeBit b = new GeneralPurposeBit();
/* 1299 */     b.useUTF8ForNames((this.useUTF8Flag) || (utfFallback));
/* 1300 */     if ((zipMethod == 8) && (this.raf == null))
/*      */     {
/* 1303 */       versionNeededToExtract = 20;
/* 1304 */       b.useDataDescriptor(true);
/*      */     }
/* 1306 */     if (zip64) {
/* 1307 */       versionNeededToExtract = 45;
/*      */     }
/*      */ 
/* 1312 */     writeOut(ZipShort.getBytes(versionNeededToExtract));
/*      */ 
/* 1314 */     writeOut(b.encode());
/*      */   }
/*      */ 
/*      */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName)
/*      */     throws IOException
/*      */   {
/* 1331 */     if (this.finished) {
/* 1332 */       throw new IOException("Stream has already been finished");
/*      */     }
/* 1334 */     return new ZipArchiveEntry(inputFile, entryName);
/*      */   }
/*      */ 
/*      */   private Zip64ExtendedInformationExtraField getZip64Extra(ZipArchiveEntry ze)
/*      */   {
/* 1345 */     if (this.entry != null) {
/* 1346 */       this.entry.causedUseOfZip64 = (!this.hasUsedZip64);
/*      */     }
/* 1348 */     this.hasUsedZip64 = true;
/* 1349 */     Zip64ExtendedInformationExtraField z64 = 
/* 1350 */       (Zip64ExtendedInformationExtraField)
/* 1351 */       ze.getExtraField(
/* 1352 */       Zip64ExtendedInformationExtraField.HEADER_ID);
/* 1353 */     if (z64 == null)
/*      */     {
/* 1360 */       z64 = new Zip64ExtendedInformationExtraField();
/*      */     }
/*      */ 
/* 1364 */     ze.addAsFirstExtraField(z64);
/*      */ 
/* 1366 */     return z64;
/*      */   }
/*      */ 
/*      */   private boolean hasZip64Extra(ZipArchiveEntry ze)
/*      */   {
/* 1376 */     return ze.getExtraField(
/* 1377 */       Zip64ExtendedInformationExtraField.HEADER_ID) != null;
/*      */   }
/*      */ 
/*      */   private Zip64Mode getEffectiveZip64Mode(ZipArchiveEntry ze)
/*      */   {
/* 1389 */     if ((this.zip64Mode != Zip64Mode.AsNeeded) || 
/* 1390 */       (this.raf != null) || 
/* 1391 */       (ze.getMethod() != 8) || 
/* 1392 */       (ze.getSize() != -1L)) {
/* 1393 */       return this.zip64Mode;
/*      */     }
/* 1395 */     return Zip64Mode.Never;
/*      */   }
/*      */ 
/*      */   private ZipEncoding getEntryEncoding(ZipArchiveEntry ze) {
/* 1399 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 1400 */     return (!encodable) && (this.fallbackToUTF8) ? 
/* 1401 */       ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*      */   }
/*      */ 
/*      */   private ByteBuffer getName(ZipArchiveEntry ze) throws IOException {
/* 1405 */     return getEntryEncoding(ze).encode(ze.getName());
/*      */   }
/*      */ 
/*      */   void destroy()
/*      */     throws IOException
/*      */   {
/* 1416 */     if (this.raf != null) {
/* 1417 */       this.raf.close();
/*      */     }
/* 1419 */     if (this.out != null)
/* 1420 */       this.out.close();
/*      */   }
/*      */ 
/*      */   private static final class CurrentEntry
/*      */   {
/*      */     private final ZipArchiveEntry entry;
/* 1470 */     private long localDataStart = 0L;
/*      */ 
/* 1474 */     private long dataStart = 0L;
/*      */ 
/* 1479 */     private long bytesRead = 0L;
/*      */ 
/* 1483 */     private boolean causedUseOfZip64 = false;
/*      */     private boolean hasWritten;
/*      */ 
/*      */     private CurrentEntry(ZipArchiveEntry entry)
/*      */     {
/* 1460 */       this.entry = entry;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class UnicodeExtraFieldPolicy
/*      */   {
/* 1432 */     public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
/*      */ 
/* 1436 */     public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
/*      */ 
/* 1442 */     public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
/*      */     private final String name;
/*      */ 
/*      */     private UnicodeExtraFieldPolicy(String n)
/*      */     {
/* 1446 */       this.name = n;
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1450 */       return this.name;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 * JD-Core Version:    0.6.2
 */