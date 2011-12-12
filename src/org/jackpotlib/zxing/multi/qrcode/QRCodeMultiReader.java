/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jackpotlib.zxing.multi.qrcode;


import java.util.Hashtable;
import java.util.Vector;

import org.jackpotlib.zxing.BarcodeFormat;
import org.jackpotlib.zxing.BinaryBitmap;
import org.jackpotlib.zxing.NotFoundException;
import org.jackpotlib.zxing.ReaderException;
import org.jackpotlib.zxing.Result;
import org.jackpotlib.zxing.ResultMetadataType;
import org.jackpotlib.zxing.ResultPoint;
import org.jackpotlib.zxing.common.DecoderResult;
import org.jackpotlib.zxing.common.DetectorResult;
import org.jackpotlib.zxing.multi.MultipleBarcodeReader;
import org.jackpotlib.zxing.multi.qrcode.detector.MultiDetector;
import org.jackpotlib.zxing.qrcode.QRCodeReader;

/**
 * This implementation can detect and decode multiple QR Codes in an image.
 *
 * @author Sean Owen
 * @author Hannes Erven
 */
public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {

  private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];

  public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
    return decodeMultiple(image, null);
  }

  public Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws NotFoundException {
    Vector results = new Vector();
    DetectorResult[] detectorResult = new MultiDetector(image.getBlackMatrix()).detectMulti(hints);
    for (int i = 0; i < detectorResult.length; i++) {
      try {
        DecoderResult decoderResult = getDecoder().decode(detectorResult[i].getBits());
        ResultPoint[] points = detectorResult[i].getPoints();
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points,
            BarcodeFormat.QR_CODE);
        if (decoderResult.getByteSegments() != null) {
          result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, decoderResult.getByteSegments());
        }
        if (decoderResult.getECLevel() != null) {
          result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel().toString());
        }
        results.addElement(result);
      } catch (ReaderException re) {
        // ignore and continue 
      }
    }
    if (results.isEmpty()) {
      return EMPTY_RESULT_ARRAY;
    } else {
      Result[] resultArray = new Result[results.size()];
      for (int i = 0; i < results.size(); i++) {
        resultArray[i] = (Result) results.elementAt(i);
      }
      return resultArray;
    }
  }

}
