package mobile.test.homerepair.testDemo;

import android.graphics.Canvas;
import android.util.Log;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Arrays;

public class CustomXAxisRenderer extends XAxisRenderer {

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
//        String[] line = formattedLabel.split("\n");
//        Utils.drawXAxisValue(c,line[0],x,y,mAxisLabelPaint,anchor,angleDegrees);
//        Utils.drawXAxisValue(c,line[1],x+mAxisLabelPaint.getTextSize(),y+ mAxisLabelPaint.getTextSize(),mAxisLabelPaint,anchor,angleDegrees);

        String[] line2 = formattedLabel.split(" ");

        Log.e("CustomXAxisRenderer->", Arrays.toString(line2));

        Utils.drawXAxisValue(c,line2[0],x,y,mAxisLabelPaint,anchor,angleDegrees);
        Utils.drawXAxisValue(c,line2[1],x+mAxisLabelPaint.getTextSize(),y+ mAxisLabelPaint.getTextSize(),mAxisLabelPaint,anchor,angleDegrees);

    }
}
