/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public class ZipFile
/*     */ {
/*     */   private static final int HASH_SIZE = 509;
/*     */   static final int NIBLET_MASK = 15;
/*     */   static final int BYTE_SHIFT = 8;
/*     */   private static final int POS_0 = 0;
/*     */   private static final int POS_1 = 1;
/*     */   private static final int POS_2 = 2;
/*     */   private static final int POS_3 = 3;
/*  88 */   private final Map<ZipArchiveEntry, OffsetEntry> entries = new LinkedHashMap(509);
/*     */ 
/*  94 */   private final Map<String, ZipArchiveEntry> nameMap = new HashMap(509);
/*     */   private final String encoding;
/*     */   private final ZipEncoding zipEncoding;
/*     */   private final String archiveName;
/*     */   private final RandomAccessFile archive;
/*     */   private final boolean useUnicodeExtraFields;
/*     */   private boolean closed;
/*     */   private static final int CFH_LEN = 42;
/* 388 */   private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
/*     */   private static final int MIN_EOCD_SIZE = 22;
/*     */   private static final int MAX_EOCD_SIZE = 65557;
/*     */   private static final int CFD_LOCATOR_OFFSET = 16;
/*     */   private static final int ZIP64_EOCDL_LENGTH = 20;
/*     */   private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
/*     */   private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
/*     */   private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
/* 954 */   private final Comparator<ZipArchiveEntry> OFFSET_COMPARATOR = new Comparator() {
/*     */     public int compare(ZipArchiveEntry e1, ZipArchiveEntry e2) {
/* 956 */       if (e1 == e2) {
/* 957 */         return 0;
/*     */       }
/*     */ 
/* 960 */       ZipFile.OffsetEntry off1 = (ZipFile.OffsetEntry)ZipFile.this.entries.get(e1);
/* 961 */       ZipFile.OffsetEntry off2 = (ZipFile.OffsetEntry)ZipFile.this.entries.get(e2);
/* 962 */       if (off1 == null) {
/* 963 */         return 1;
/*     */       }
/* 965 */       if (off2 == null) {
/* 966 */         return -1;
/*     */       }
/* 968 */       long val = ZipFile.OffsetEntry.access$0(off1) - ZipFile.OffsetEntry.access$0(off2);
/* 969 */       return val < 0L ? -1 : val == 0L ? 0 : 1;
/*     */     }
/* 954 */   };
/*     */ 
/*     */   public ZipFile(File f)
/*     */     throws IOException
/*     */   {
/* 143 */     this(f, "UTF8");
/*     */   }
/*     */ 
/*     */   public ZipFile(String name)
/*     */     throws IOException
/*     */   {
/* 154 */     this(new File(name), "UTF8");
/*     */   }
/*     */ 
/*     */   public ZipFile(String name, String encoding)
/*     */     throws IOException
/*     */   {
/* 168 */     this(new File(name), encoding, true);
/*     */   }
/*     */ 
/*     */   public ZipFile(File f, String encoding)
/*     */     throws IOException
/*     */   {
/* 182 */     this(f, encoding, true);
/*     */   }
/*     */ 
/*     */   public ZipFile(File f, String encoding, boolean useUnicodeExtraFields)
/*     */     throws IOException
/*     */   {
/* 199 */     this.archiveName = f.getAbsolutePath();
/* 200 */     this.encoding = encoding;
/* 201 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 202 */     this.useUnicodeExtraFields = useUnicodeExtraFields;
/* 203 */     this.archive = new RandomAccessFile(f, "r");
/* 204 */     boolean success = false;
/*     */     try {
/* 206 */       Map entriesWithoutUTF8Flag = 
/* 207 */         populateFromCentralDirectory();
/* 208 */       resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
/* 209 */       success = true;
/*     */     } finally {
/* 211 */       if (!success)
/*     */         try {
/* 213 */           this.closed = true;
/* 214 */           this.archive.close();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getEncoding()
/*     */   {
/* 228 */     return this.encoding;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 239 */     this.closed = true;
/*     */ 
/* 241 */     this.archive.close();
/*     */   }
/*     */ 
/*     */   public static void closeQuietly(ZipFile zipfile)
/*     */   {
/* 250 */     if (zipfile != null)
/*     */       try {
/* 252 */         zipfile.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public Enumeration<ZipArchiveEntry> getEntries()
/*     */   {
/* 268 */     return Collections.enumeration(this.entries.keySet());
/*     */   }
/*     */ 
/*     */   public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder()
/*     */   {
/* 282 */     ZipArchiveEntry[] allEntries = 
/* 283 */       (ZipArchiveEntry[])this.entries.keySet().toArray(new ZipArchiveEntry[0]);
/* 284 */     Arrays.sort(allEntries, this.OFFSET_COMPARATOR);
/* 285 */     return Collections.enumeration(Arrays.asList(allEntries));
/*     */   }
/*     */ 
/*     */   public ZipArchiveEntry getEntry(String name)
/*     */   {
/* 296 */     return (ZipArchiveEntry)this.nameMap.get(name);
/*     */   }
/*     */ 
/*     */   public boolean canReadEntryData(ZipArchiveEntry ze)
/*     */   {
/* 307 */     return ZipUtil.canHandleEntryData(ze);
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(ZipArchiveEntry ze)
/*     */     throws IOException, ZipException
/*     */   {
/* 320 */     OffsetEntry offsetEntry = (OffsetEntry)this.entries.get(ze);
/* 321 */     if (offsetEntry == null) {
/* 322 */       return null;
/*     */     }
/* 324 */     ZipUtil.checkRequestedFeatures(ze);
/* 325 */     long start = offsetEntry.dataOffset;
/* 326 */     BoundedInputStream bis = 
/* 327 */       new BoundedInputStream(start, ze.getCompressedSize());
/* 328 */     switch (ze.getMethod()) {
/*     */     case 0:
/* 330 */       return bis;
/*     */     case 8:
/* 332 */       bis.addDummy();
/* 333 */       final Inflater inflater = new Inflater(true);
/* 334 */       return new InflaterInputStream(bis, inflater)
/*     */       {
/*     */         public void close() throws IOException {
/* 337 */           super.close();
/* 338 */           inflater.end();
/*     */         }
/*     */       };
/*     */     }
/* 342 */     throw new ZipException("Found unsupported compression method " + 
/* 343 */       ze.getMethod());
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 355 */       if (!this.closed) {
/* 356 */         System.err.println("Cleaning up unclosed ZipFile for archive " + 
/* 357 */           this.archiveName);
/* 358 */         close();
/*     */       }
/*     */     } finally {
/* 361 */       super.finalize();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory()
/*     */     throws IOException
/*     */   {
/* 403 */     HashMap noUTF8Flag = 
/* 404 */       new HashMap();
/*     */ 
/* 406 */     positionAtCentralDirectory();
/*     */ 
/* 408 */     byte[] signatureBytes = new byte[4];
/* 409 */     this.archive.readFully(signatureBytes);
/* 410 */     long sig = ZipLong.getValue(signatureBytes);
/*     */ 
/* 412 */     if ((sig != CFH_SIG) && (startsWithLocalFileHeader())) {
/* 413 */       throw new IOException("central directory is empty, can't expand corrupt archive.");
/*     */     }
/*     */ 
/* 417 */     while (sig == CFH_SIG) {
/* 418 */       readCentralDirectoryEntry(noUTF8Flag);
/* 419 */       this.archive.readFully(signatureBytes);
/* 420 */       sig = ZipLong.getValue(signatureBytes);
/*     */     }
/* 422 */     return noUTF8Flag;
/*     */   }
/*     */ 
/*     */   private void readCentralDirectoryEntry(Map<ZipArchiveEntry, NameAndComment> noUTF8Flag)
/*     */     throws IOException
/*     */   {
/* 437 */     byte[] cfh = new byte[42];
/*     */ 
/* 439 */     this.archive.readFully(cfh);
/* 440 */     int off = 0;
/* 441 */     ZipArchiveEntry ze = new ZipArchiveEntry();
/*     */ 
/* 443 */     int versionMadeBy = ZipShort.getValue(cfh, off);
/* 444 */     off += 2;
/* 445 */     ze.setPlatform(versionMadeBy >> 8 & 0xF);
/*     */ 
/* 447 */     off += 2;
/*     */ 
/* 449 */     GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(cfh, off);
/* 450 */     boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
/* 451 */     ZipEncoding entryEncoding = 
/* 452 */       hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/* 453 */     ze.setGeneralPurposeBit(gpFlag);
/*     */ 
/* 455 */     off += 2;
/*     */ 
/* 457 */     ze.setMethod(ZipShort.getValue(cfh, off));
/* 458 */     off += 2;
/*     */ 
/* 460 */     long time = ZipUtil.dosToJavaTime(ZipLong.getValue(cfh, off));
/* 461 */     ze.setTime(time);
/* 462 */     off += 4;
/*     */ 
/* 464 */     ze.setCrc(ZipLong.getValue(cfh, off));
/* 465 */     off += 4;
/*     */ 
/* 467 */     ze.setCompressedSize(ZipLong.getValue(cfh, off));
/* 468 */     off += 4;
/*     */ 
/* 470 */     ze.setSize(ZipLong.getValue(cfh, off));
/* 471 */     off += 4;
/*     */ 
/* 473 */     int fileNameLen = ZipShort.getValue(cfh, off);
/* 474 */     off += 2;
/*     */ 
/* 476 */     int extraLen = ZipShort.getValue(cfh, off);
/* 477 */     off += 2;
/*     */ 
/* 479 */     int commentLen = ZipShort.getValue(cfh, off);
/* 480 */     off += 2;
/*     */ 
/* 482 */     int diskStart = ZipShort.getValue(cfh, off);
/* 483 */     off += 2;
/*     */ 
/* 485 */     ze.setInternalAttributes(ZipShort.getValue(cfh, off));
/* 486 */     off += 2;
/*     */ 
/* 488 */     ze.setExternalAttributes(ZipLong.getValue(cfh, off));
/* 489 */     off += 4;
/*     */ 
/* 491 */     byte[] fileName = new byte[fileNameLen];
/* 492 */     this.archive.readFully(fileName);
/* 493 */     ze.setName(entryEncoding.decode(fileName), fileName);
/*     */ 
/* 496 */     OffsetEntry offset = new OffsetEntry(null);
/* 497 */     offset.headerOffset = ZipLong.getValue(cfh, off);
/*     */ 
/* 499 */     this.entries.put(ze, offset);
/*     */ 
/* 501 */     this.nameMap.put(ze.getName(), ze);
/*     */ 
/* 503 */     byte[] cdExtraData = new byte[extraLen];
/* 504 */     this.archive.readFully(cdExtraData);
/* 505 */     ze.setCentralDirectoryExtra(cdExtraData);
/*     */ 
/* 507 */     setSizesAndOffsetFromZip64Extra(ze, offset, diskStart);
/*     */ 
/* 509 */     byte[] comment = new byte[commentLen];
/* 510 */     this.archive.readFully(comment);
/* 511 */     ze.setComment(entryEncoding.decode(comment));
/*     */ 
/* 513 */     if ((!hasUTF8Flag) && (this.useUnicodeExtraFields))
/* 514 */       noUTF8Flag.put(ze, new NameAndComment(fileName, comment, null));
/*     */   }
/*     */ 
/*     */   private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze, OffsetEntry offset, int diskStart)
/*     */     throws IOException
/*     */   {
/* 534 */     Zip64ExtendedInformationExtraField z64 = 
/* 535 */       (Zip64ExtendedInformationExtraField)
/* 536 */       ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/* 537 */     if (z64 != null) {
/* 538 */       boolean hasUncompressedSize = ze.getSize() == 4294967295L;
/* 539 */       boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
/* 540 */       boolean hasRelativeHeaderOffset = 
/* 541 */         offset.headerOffset == 4294967295L;
/* 542 */       z64.reparseCentralDirectoryData(hasUncompressedSize, 
/* 543 */         hasCompressedSize, 
/* 544 */         hasRelativeHeaderOffset, 
/* 545 */         diskStart == 65535);
/*     */ 
/* 547 */       if (hasUncompressedSize)
/* 548 */         ze.setSize(z64.getSize().getLongValue());
/* 549 */       else if (hasCompressedSize) {
/* 550 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*     */       }
/*     */ 
/* 553 */       if (hasCompressedSize)
/* 554 */         ze.setCompressedSize(z64.getCompressedSize().getLongValue());
/* 555 */       else if (hasUncompressedSize) {
/* 556 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/*     */       }
/*     */ 
/* 559 */       if (hasRelativeHeaderOffset)
/* 560 */         offset.headerOffset = 
/* 561 */           z64.getRelativeHeaderOffset().getLongValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void positionAtCentralDirectory()
/*     */     throws IOException
/*     */   {
/* 666 */     boolean found = tryToLocateSignature(42L, 
/* 667 */       65577L, 
/* 669 */       ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG);
/* 670 */     if (!found)
/*     */     {
/* 672 */       positionAtCentralDirectory32();
/*     */     }
/* 674 */     else positionAtCentralDirectory64();
/*     */   }
/*     */ 
/*     */   private void positionAtCentralDirectory64()
/*     */     throws IOException
/*     */   {
/* 686 */     skipBytes(8);
/* 687 */     byte[] zip64EocdOffset = new byte[8];
/* 688 */     this.archive.readFully(zip64EocdOffset);
/* 689 */     this.archive.seek(ZipEightByteInteger.getLongValue(zip64EocdOffset));
/* 690 */     byte[] sig = new byte[4];
/* 691 */     this.archive.readFully(sig);
/* 692 */     if ((sig[0] != ZipArchiveOutputStream.ZIP64_EOCD_SIG[0]) || 
/* 693 */       (sig[1] != ZipArchiveOutputStream.ZIP64_EOCD_SIG[1]) || 
/* 694 */       (sig[2] != ZipArchiveOutputStream.ZIP64_EOCD_SIG[2]) || 
/* 695 */       (sig[3] != ZipArchiveOutputStream.ZIP64_EOCD_SIG[3]))
/*     */     {
/* 697 */       throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
/*     */     }
/*     */ 
/* 700 */     skipBytes(44);
/*     */ 
/* 702 */     byte[] cfdOffset = new byte[8];
/* 703 */     this.archive.readFully(cfdOffset);
/* 704 */     this.archive.seek(ZipEightByteInteger.getLongValue(cfdOffset));
/*     */   }
/*     */ 
/*     */   private void positionAtCentralDirectory32()
/*     */     throws IOException
/*     */   {
/* 714 */     boolean found = tryToLocateSignature(22L, 65557L, 
/* 715 */       ZipArchiveOutputStream.EOCD_SIG);
/* 716 */     if (!found) {
/* 717 */       throw new ZipException("archive is not a ZIP archive");
/*     */     }
/* 719 */     skipBytes(16);
/* 720 */     byte[] cfdOffset = new byte[4];
/* 721 */     this.archive.readFully(cfdOffset);
/* 722 */     this.archive.seek(ZipLong.getValue(cfdOffset));
/*     */   }
/*     */ 
/*     */   private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig)
/*     */     throws IOException
/*     */   {
/* 733 */     boolean found = false;
/* 734 */     long off = this.archive.length() - minDistanceFromEnd;
/* 735 */     long stopSearching = 
/* 736 */       Math.max(0L, this.archive.length() - maxDistanceFromEnd);
/* 737 */     if (off >= 0L) {
/* 738 */       for (; off >= stopSearching; off -= 1L) {
/* 739 */         this.archive.seek(off);
/* 740 */         int curr = this.archive.read();
/* 741 */         if (curr == -1) {
/*     */           break;
/*     */         }
/* 744 */         if (curr == sig[0]) {
/* 745 */           curr = this.archive.read();
/* 746 */           if (curr == sig[1]) {
/* 747 */             curr = this.archive.read();
/* 748 */             if (curr == sig[2]) {
/* 749 */               curr = this.archive.read();
/* 750 */               if (curr == sig[3]) {
/* 751 */                 found = true;
/* 752 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 759 */     if (found) {
/* 760 */       this.archive.seek(off);
/*     */     }
/* 762 */     return found;
/*     */   }
/*     */ 
/*     */   private void skipBytes(int count)
/*     */     throws IOException
/*     */   {
/* 770 */     int totalSkipped = 0;
/* 771 */     while (totalSkipped < count) {
/* 772 */       int skippedNow = this.archive.skipBytes(count - totalSkipped);
/* 773 */       if (skippedNow <= 0) {
/* 774 */         throw new EOFException();
/*     */       }
/* 776 */       totalSkipped += skippedNow;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resolveLocalFileHeaderData(Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag)
/*     */     throws IOException
/*     */   {
/* 809 */     Map origMap = 
/* 810 */       new LinkedHashMap(this.entries);
/* 811 */     this.entries.clear();
/* 812 */     for (Map.Entry ent : origMap.entrySet()) {
/* 813 */       ZipArchiveEntry ze = (ZipArchiveEntry)ent.getKey();
/* 814 */       OffsetEntry offsetEntry = (OffsetEntry)ent.getValue();
/* 815 */       long offset = offsetEntry.headerOffset;
/* 816 */       this.archive.seek(offset + 26L);
/* 817 */       byte[] b = new byte[2];
/* 818 */       this.archive.readFully(b);
/* 819 */       int fileNameLen = ZipShort.getValue(b);
/* 820 */       this.archive.readFully(b);
/* 821 */       int extraFieldLen = ZipShort.getValue(b);
/* 822 */       int lenToSkip = fileNameLen;
/* 823 */       while (lenToSkip > 0) {
/* 824 */         int skipped = this.archive.skipBytes(lenToSkip);
/* 825 */         if (skipped <= 0) {
/* 826 */           throw new IOException("failed to skip file name in local file header");
/*     */         }
/*     */ 
/* 829 */         lenToSkip -= skipped;
/*     */       }
/* 831 */       byte[] localExtraData = new byte[extraFieldLen];
/* 832 */       this.archive.readFully(localExtraData);
/* 833 */       ze.setExtra(localExtraData);
/* 834 */       offsetEntry.dataOffset = 
/* 835 */         (offset + 26L + 
/* 835 */         2L + 2L + fileNameLen + extraFieldLen);
/*     */ 
/* 837 */       if (entriesWithoutUTF8Flag.containsKey(ze)) {
/* 838 */         String orig = ze.getName();
/* 839 */         NameAndComment nc = (NameAndComment)entriesWithoutUTF8Flag.get(ze);
/* 840 */         ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, 
/* 841 */           nc.comment);
/* 842 */         if (!orig.equals(ze.getName())) {
/* 843 */           this.nameMap.remove(orig);
/* 844 */           this.nameMap.put(ze.getName(), ze);
/*     */         }
/*     */       }
/* 847 */       this.entries.put(ze, offsetEntry);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean startsWithLocalFileHeader()
/*     */     throws IOException
/*     */   {
/* 856 */     this.archive.seek(0L);
/* 857 */     byte[] start = new byte[4];
/* 858 */     this.archive.readFully(start);
/* 859 */     for (int i = 0; i < start.length; i++) {
/* 860 */       if (start[i] != ZipArchiveOutputStream.LFH_SIG[i]) {
/* 861 */         return false;
/*     */       }
/*     */     }
/* 864 */     return true;
/*     */   }
/*     */ 
/*     */   private class BoundedInputStream extends InputStream
/*     */   {
/*     */     private long remaining;
/*     */     private long loc;
/* 875 */     private boolean addDummyByte = false;
/*     */ 
/*     */     BoundedInputStream(long start, long remaining) {
/* 878 */       this.remaining = remaining;
/* 879 */       this.loc = start;
/*     */     }
/*     */ 
/*     */     public int read() throws IOException
/*     */     {
/* 884 */       if (this.remaining-- <= 0L) {
/* 885 */         if (this.addDummyByte) {
/* 886 */           this.addDummyByte = false;
/* 887 */           return 0;
/*     */         }
/* 889 */         return -1;
/*     */       }
/* 891 */       synchronized (ZipFile.this.archive) {
/* 892 */         ZipFile.this.archive.seek(this.loc++);
/* 893 */         return ZipFile.this.archive.read();
/*     */       }
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len) throws IOException
/*     */     {
/* 899 */       if (this.remaining <= 0L) {
/* 900 */         if (this.addDummyByte) {
/* 901 */           this.addDummyByte = false;
/* 902 */           b[off] = 0;
/* 903 */           return 1;
/*     */         }
/* 905 */         return -1;
/*     */       }
/*     */ 
/* 908 */       if (len <= 0) {
/* 909 */         return 0;
/*     */       }
/*     */ 
/* 912 */       if (len > this.remaining) {
/* 913 */         len = (int)this.remaining;
/*     */       }
/* 915 */       int ret = -1;
/* 916 */       synchronized (ZipFile.this.archive) {
/* 917 */         ZipFile.this.archive.seek(this.loc);
/* 918 */         ret = ZipFile.this.archive.read(b, off, len);
/*     */       }
/* 920 */       if (ret > 0) {
/* 921 */         this.loc += ret;
/* 922 */         this.remaining -= ret;
/*     */       }
/* 924 */       return ret;
/*     */     }
/*     */ 
/*     */     void addDummy()
/*     */     {
/* 932 */       this.addDummyByte = true;
/*     */     }
/*     */   }
/*     */   private static final class NameAndComment {
/*     */     private final byte[] name;
/*     */     private final byte[] comment;
/*     */ 
/* 940 */     private NameAndComment(byte[] name, byte[] comment) { this.name = name;
/* 941 */       this.comment = comment;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OffsetEntry
/*     */   {
/*  97 */     private long headerOffset = -1L;
/*  98 */     private long dataOffset = -1L;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipFile
 * JD-Core Version:    0.6.2
 */