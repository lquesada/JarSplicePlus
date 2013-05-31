/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public class ExtraFieldUtils
/*     */ {
/*     */   private static final int WORD = 4;
/*  41 */   private static final Map<ZipShort, Class<?>> implementations = new HashMap();
/*     */ 
/*  42 */   static { register(AsiExtraField.class);
/*  43 */     register(JarMarker.class);
/*  44 */     register(UnicodePathExtraField.class);
/*  45 */     register(UnicodeCommentExtraField.class);
/*  46 */     register(Zip64ExtendedInformationExtraField.class);
/*     */   }
/*     */ 
/*     */   public static void register(Class<?> c)
/*     */   {
/*     */     try
/*     */     {
/*  58 */       ZipExtraField ze = (ZipExtraField)c.newInstance();
/*  59 */       implementations.put(ze.getHeaderId(), c);
/*     */     } catch (ClassCastException cc) {
/*  61 */       throw new RuntimeException(c + " doesn't implement ZipExtraField");
/*     */     } catch (InstantiationException ie) {
/*  63 */       throw new RuntimeException(c + " is not a concrete class");
/*     */     } catch (IllegalAccessException ie) {
/*  65 */       throw new RuntimeException(c + "'s no-arg constructor is not public");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ZipExtraField createExtraField(ZipShort headerId)
/*     */     throws InstantiationException, IllegalAccessException
/*     */   {
/*  79 */     Class c = (Class)implementations.get(headerId);
/*  80 */     if (c != null) {
/*  81 */       return (ZipExtraField)c.newInstance();
/*     */     }
/*  83 */     UnrecognizedExtraField u = new UnrecognizedExtraField();
/*  84 */     u.setHeaderId(headerId);
/*  85 */     return u;
/*     */   }
/*     */ 
/*     */   public static ZipExtraField[] parse(byte[] data)
/*     */     throws ZipException
/*     */   {
/*  97 */     return parse(data, true, UnparseableExtraField.THROW);
/*     */   }
/*     */ 
/*     */   public static ZipExtraField[] parse(byte[] data, boolean local)
/*     */     throws ZipException
/*     */   {
/* 111 */     return parse(data, local, UnparseableExtraField.THROW);
/*     */   }
/*     */ 
/*     */   public static ZipExtraField[] parse(byte[] data, boolean local, UnparseableExtraField onUnparseableData)
/*     */     throws ZipException
/*     */   {
/* 130 */     List v = new ArrayList();
/* 131 */     int start = 0;
/*     */ 
/* 133 */     while (start <= data.length - 4) {
/* 134 */       ZipShort headerId = new ZipShort(data, start);
/* 135 */       int length = new ZipShort(data, start + 2).getValue();
/* 136 */       if (start + 4 + length > data.length)
/* 137 */         switch (onUnparseableData.getKey()) {
/*     */         case 0:
/* 139 */           throw new ZipException("bad extra field starting at " + 
/* 140 */             start + ".  Block length of " + 
/* 141 */             length + " bytes exceeds remaining" + 
/* 142 */             " data of " + (
/* 143 */             data.length - start - 4) + 
/* 144 */             " bytes.");
/*     */         case 2:
/* 146 */           UnparseableExtraFieldData field = 
/* 147 */             new UnparseableExtraFieldData();
/* 148 */           if (local)
/* 149 */             field.parseFromLocalFileData(data, start, 
/* 150 */               data.length - start);
/*     */           else {
/* 152 */             field.parseFromCentralDirectoryData(data, start, 
/* 153 */               data.length - start);
/*     */           }
/* 155 */           v.add(field);
/*     */         case 1:
/* 161 */           break;
/*     */         default:
/* 163 */           throw new ZipException("unknown UnparseableExtraField key: " + 
/* 164 */             onUnparseableData.getKey());
/*     */         }
/*     */       try
/*     */       {
/* 168 */         ZipExtraField ze = createExtraField(headerId);
/* 169 */         if (local)
/* 170 */           ze.parseFromLocalFileData(data, start + 4, length);
/*     */         else {
/* 172 */           ze.parseFromCentralDirectoryData(data, start + 4, 
/* 173 */             length);
/*     */         }
/* 175 */         v.add(ze);
/*     */       } catch (InstantiationException ie) {
/* 177 */         throw new ZipException(ie.getMessage());
/*     */       } catch (IllegalAccessException iae) {
/* 179 */         throw new ZipException(iae.getMessage());
/*     */       }
/* 181 */       start += length + 4;
/*     */     }
/*     */ 
/* 184 */     ZipExtraField[] result = new ZipExtraField[v.size()];
/* 185 */     return (ZipExtraField[])v.toArray(result);
/*     */   }
/*     */ 
/*     */   public static byte[] mergeLocalFileDataData(ZipExtraField[] data)
/*     */   {
/* 194 */     boolean lastIsUnparseableHolder = (data.length > 0) && 
/* 195 */       ((data[(data.length - 1)] instanceof UnparseableExtraFieldData));
/* 196 */     int regularExtraFieldCount = 
/* 197 */       lastIsUnparseableHolder ? data.length - 1 : data.length;
/*     */ 
/* 199 */     int sum = 4 * regularExtraFieldCount;
/* 200 */     ZipExtraField[] arrayOfZipExtraField = data; int j = data.length; for (int i = 0; i < j; i++) { ZipExtraField element = arrayOfZipExtraField[i];
/* 201 */       sum += element.getLocalFileDataLength().getValue();
/*     */     }
/*     */ 
/* 204 */     byte[] result = new byte[sum];
/* 205 */     int start = 0;
/* 206 */     for (int i = 0; i < regularExtraFieldCount; i++) {
/* 207 */       System.arraycopy(data[i].getHeaderId().getBytes(), 
/* 208 */         0, result, start, 2);
/* 209 */       System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 
/* 210 */         0, result, start + 2, 2);
/* 211 */       byte[] local = data[i].getLocalFileDataData();
/* 212 */       System.arraycopy(local, 0, result, start + 4, local.length);
/* 213 */       start += local.length + 4;
/*     */     }
/* 215 */     if (lastIsUnparseableHolder) {
/* 216 */       byte[] local = data[(data.length - 1)].getLocalFileDataData();
/* 217 */       System.arraycopy(local, 0, result, start, local.length);
/*     */     }
/* 219 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] mergeCentralDirectoryData(ZipExtraField[] data)
/*     */   {
/* 228 */     boolean lastIsUnparseableHolder = (data.length > 0) && 
/* 229 */       ((data[(data.length - 1)] instanceof UnparseableExtraFieldData));
/* 230 */     int regularExtraFieldCount = 
/* 231 */       lastIsUnparseableHolder ? data.length - 1 : data.length;
/*     */ 
/* 233 */     int sum = 4 * regularExtraFieldCount;
/* 234 */     ZipExtraField[] arrayOfZipExtraField = data; int j = data.length; for (int i = 0; i < j; i++) { ZipExtraField element = arrayOfZipExtraField[i];
/* 235 */       sum += element.getCentralDirectoryLength().getValue();
/*     */     }
/* 237 */     byte[] result = new byte[sum];
/* 238 */     int start = 0;
/* 239 */     for (int i = 0; i < regularExtraFieldCount; i++) {
/* 240 */       System.arraycopy(data[i].getHeaderId().getBytes(), 
/* 241 */         0, result, start, 2);
/* 242 */       System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 
/* 243 */         0, result, start + 2, 2);
/* 244 */       byte[] local = data[i].getCentralDirectoryData();
/* 245 */       System.arraycopy(local, 0, result, start + 4, local.length);
/* 246 */       start += local.length + 4;
/*     */     }
/* 248 */     if (lastIsUnparseableHolder) {
/* 249 */       byte[] local = data[(data.length - 1)].getCentralDirectoryData();
/* 250 */       System.arraycopy(local, 0, result, start, local.length);
/*     */     }
/* 252 */     return result;
/*     */   }
/*     */ 
/*     */   public static final class UnparseableExtraField
/*     */   {
/*     */     public static final int THROW_KEY = 0;
/*     */     public static final int SKIP_KEY = 1;
/*     */     public static final int READ_KEY = 2;
/* 279 */     public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
/*     */ 
/* 286 */     public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
/*     */ 
/* 293 */     public static final UnparseableExtraField READ = new UnparseableExtraField(2);
/*     */     private final int key;
/*     */ 
/*     */     private UnparseableExtraField(int k)
/*     */     {
/* 298 */       this.key = k;
/*     */     }
/*     */ 
/*     */     public int getKey()
/*     */     {
/* 304 */       return this.key;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ExtraFieldUtils
 * JD-Core Version:    0.6.2
 */