package compx576.assignment.httydgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

// Class that creates a typewriter effect for a TextView
@SuppressLint("AppCompatCustomView")
public class TypeWriter extends TextView {

    // Global variables
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150; //Default 150ms delay

    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Create a thread, and print a new character every n milliseconds.
    private final Handler mHandler = new Handler();
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    // Animate the text by printing a new character in the character sequence every n milliseconds
    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    // Set the delay
    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    // Skip to the end of a piece of dialogue
    public void removeDelay() {
        mHandler.removeCallbacks(characterAdder);
        setText(mText);
    }
}
