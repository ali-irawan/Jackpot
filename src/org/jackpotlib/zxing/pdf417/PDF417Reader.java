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

package org.jackpotlib.zxing.pdf417;


import java.util.Hashtable;

import org.jackpotlib.zxing.BarcodeFormat;
import org.jackpotlib.zxing.BinaryBitmap;
import org.jackpotlib.zxing.DecodeHintType;
import org.jackpotlib.zxing.FormatException;
import org.jackpotlib.zxing.NotFoundException;
import org.jackpotlib.zxing.Reader;
import org.jackpotlib.zxing.Result;
import org.jackpotlib.zxing.ResultPoint;
import org.jackpotlib.zxing.common.BitMatrix;
import org.jackpotlib.zxing.common.DecoderResult;
import org.jackpotlib.zxing.common.DetectorResult;
import org.jackpotlib.zxing.pdf417.decoder.Decoder;
import org.jackpotlib.zxing.pdf417.detector.Detector;
import org.jackpotlib.zxing.qrcode.QRCodeReader;

/**
 * This implementation can detect and decode PDF417 codes in an image.
 *
 * @author SITA Lab (kevin.osullivan@sita.aero)
 */
public final class PDF417Reader implements Reader {

  private static final ResultPoint[] NO_POINTS = new ResultPoint[0];

  private final Decoder decoder = new Decoder();

  /**
   * Locates and decodes a PDF417 code in an image.
   *
   * @return a String representing the content encoded by the PDF417 code
   * @throws NotFoundException if a PDF417 code cannot be found,
   * @throws FormatException if a PDF417 cannot be decoded
   */
  public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
    return decode(image, null);
  }

  public Result decode(BinaryBitmap image, Hashtable hints)
      throws NotFoundException, FormatException {
    DecoderResult decoderResult;
    ResultPoint[] points;
    if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
      BitMatrix bits = QRCodeReader.extractPureBits(image.getBlackMatrix());
      decoderResult = decoder.decode(bits);
      points = NO_POINTS;
    } else {
      DetectorResult detectorResult = new Detector(image).detect();
      decoderResult = decoder.decode(detectorResult.getBits());
      points = detectorResult.getPoints();
    }
    return new Result(decoderResult.getText(), decoderResult.getRawBytes(), points,
        BarcodeFormat.PDF417);
  }

  public void reset() {
    // do nothing
  }

}
