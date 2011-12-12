package org.jackpotlib.barcodelib;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

import org.jackpotlib.zxing.BinaryBitmap;
import org.jackpotlib.zxing.DecodeHintType;
import org.jackpotlib.zxing.LuminanceSource;
import org.jackpotlib.zxing.MultiFormatReader;
import org.jackpotlib.zxing.NotFoundException;
import org.jackpotlib.zxing.Result;
import org.jackpotlib.zxing.common.HybridBinarizer;


/**
 * Barcode scanner main class
 * 
 * @author Ali Irawan (boylevantz@gmail.com)
 * @vesion 1.0
 */
public final class BarcodeScanner {
	private BarcodeDecoder _decoder;
	private BarcodeDecoderListener _listener;
	private VideoControl _vc;
	private Player _player;
	private Field _viewFinder;
	private String _encoding;
	private PopupScreen _waitingScreen;

	/**
	 * Create the barcode scanner instance
	 * 
	 * @param decoder
	 *            specify the decoder instance
	 * @param listener
	 *            listener for handling events
	 * @throws IOException
	 *             occurred when video support is unavailable
	 * @throws MediaException
	 *             occurred when video cannot be started
	 */
	public BarcodeScanner(BarcodeDecoder decoder,
			BarcodeDecoderListener listener) throws IOException, MediaException {
		_decoder = decoder;
		_listener = listener;
		init();

	}

	/*
	 * initialize resource
	 */
	private void init() throws IOException, MediaException {
		_player = Manager.createPlayer("capture://video");
		_player.realize();
		_player.start();
		_vc = (VideoControl) _player.getControl("VideoControl");

		_viewFinder = (Field) _vc.initDisplayMode(
				VideoControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
		
	}

	/**
	 * Get the player instance
	 * 
	 * @return player instance
	 */
	public Player getPlayer() {
		return _player;
	}

	/**
	 * Get the video control
	 * 
	 * @return video control instance
	 */
	public VideoControl getVideoControl() {
		return _vc;
	}

	/**
	 * Get the view finder
	 * 
	 * @return View finder instance
	 */
	public Field getViewFinder() {
		return _viewFinder;
	}

	/**
	 * Start scanning
	 * 
	 * @throws MediaException
	 *             occurred when Video Control is not initialized yet
	 */
	public void startScan() throws MediaException {
		setupDefaultEncoding();
		if (_vc != null) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					_viewFinder.setFocus();
				}
			});
			_vc.setVisible(true);
		} else {
			throw new MediaException("Video Control is not initialized");
		}
	}

	/**
	 * Stop scanning
	 * 
	 * @throws MediaException
	 */
	public void stopScan() throws MediaException {
		if (_vc != null) {
			try {
				if (_viewFinder == null) {
					throw new MediaException("View finder is not exists");
				}
				// get snapshot of the view finder
				final byte[] imageBytes = _vc.getSnapshot(_encoding);

				// show waiting screen
				if (_waitingScreen == null) {
					HorizontalFieldManager manager = new HorizontalFieldManager();
					manager.add(new LabelField("Please Wait..."));
					_waitingScreen = new PopupScreen(manager);
				}
				UiApplication.getUiApplication().pushScreen(_waitingScreen);

				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						/*
						try {
							Thread.currentThread().sleep(2000); //assume 2 seconds neeed to decode, just for simulation
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}*/

						if (imageBytes.length > 0) {

							// create image from bytes
							Bitmap capturedBitmap = Bitmap
									.createBitmapFromBytes(imageBytes, 0,
											imageBytes.length, 1);

							MultiFormatReader reader = new MultiFormatReader();

							// creating luminance source
							LuminanceSource source = new BitmapLuminanceSource(
									capturedBitmap);
							BinaryBitmap bitmap = new BinaryBitmap(
									new HybridBinarizer(source));

							Hashtable hints = null;
							if (_decoder != null) {
								hints = _decoder._hints;
							}

							if (hints == null) {
								hints = new Hashtable(1);
								hints.put(DecodeHintType.TRY_HARDER,
										Boolean.TRUE);
							}

							Result result;
							String rawText = "";
							try {
								result = reader.decode(bitmap, hints);
								rawText = result.getText();
								_listener.barcodeDecoded(rawText);
							} catch (NotFoundException e) {
								_listener.barcodeFailDecoded(e);
								// e.printStackTrace();
							}
						}
						UiApplication.getUiApplication().popScreen(_waitingScreen);
						_listener.barcodeDecodeProcessFinish();
					}
				});
			} catch (Exception ex) {
				throw new MediaException(ex.getMessage());
			}
		} else {
			throw new MediaException("Video Control is not initialized");
		}
	}

	/**
	 * Default setup encoding
	 */
	public void setupDefaultEncoding() {
		// System.getProperty("video.snapshot.encodings");
		_encoding = "encoding=jpeg&width=640&height=480&quality=superfine";
	}

	/**
	 * Setup specified encoding e.g.
	 * &quot;encoding=jpeg&width=640&height=480&quality=superfine&quot;
	 * 
	 * @param encodingType
	 */
	public void setupEncoding(String encodingType) {
		_encoding = encodingType;
	}

}
