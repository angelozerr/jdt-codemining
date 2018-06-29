package org.eclipse.jface.text.revisions.provisional.avatar;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class Avatar {

	private byte[] byteArray;
	
	private ImageData data;

	public Avatar(String hash, long currentTimeMillis, byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ImageData getData() {
		if (this.data != null)
			return this.data;

		try {
			ImageData[] images = new ImageLoader()
					.load(new ByteArrayInputStream(byteArray));
			if (images.length > 0)
				this.data = images[0];
			else
				this.data = ImageDescriptor.getMissingImageDescriptor()
						.getImageData();
		} catch (SWTException exception) {
			this.data = ImageDescriptor.getMissingImageDescriptor()
					.getImageData();
		}
		return this.data;
	}

}
