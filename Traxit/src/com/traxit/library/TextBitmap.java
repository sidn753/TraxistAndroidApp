package com.traxit.library;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;

public class TextBitmap {
	public TextBitmap(){
		
	}
	public Bitmap createBitmapWithText(String mFullName){
		if(mFullName !=null && !mFullName.isEmpty()){
			String[] separated = mFullName.split(" ");
			List<String> names = eatSpaceFromArray(separated);
			String shortName;
			String firstName = names.get(0).substring(0,1);
			if(names.size() >1){
				shortName = firstName+ names.get(1).substring(0,1);
			}else{
				shortName = firstName;
			}
			shortName = shortName.toUpperCase();
			return drawText(shortName, 80, 50);
		}else{
			return null;
		}
		
		
	}
    private Bitmap drawText(String text, int textWidth, int textSize) {
    // Get text dimensions
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
    | Paint.LINEAR_TEXT_FLAG);
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(textSize);
    Typeface font  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
    textPaint.setTypeface(font);
    StaticLayout mTextLayout = new StaticLayout(text, textPaint,
    textWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    
     
    // Create bitmap and canvas to draw to
    Bitmap b = Bitmap.createBitmap(textWidth, textWidth, Config.RGB_565);
    Canvas c = new Canvas(b);
     
    // Draw background
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
    | Paint.LINEAR_TEXT_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.GRAY);
    c.drawPaint(paint);
     
    // Draw text
    c.save();
    c.translate(0, 10);
    mTextLayout.draw(c);
    c.restore();
     
    return b;
    }
    
   private List<String>eatSpaceFromArray(String[] array){
	   List<String> result = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
        	String item = array[i];
            if (item !=null && !item.isEmpty()) {
            	result.add(item);
            }
        }
        return result;
    }
}
