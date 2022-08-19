package mobile.test.homerepair.testDemo;

public class TestLineChartDataObject {


    String lineChartID;
    int xValue;
    int yValue;



    public TestLineChartDataObject() {
    }


    public TestLineChartDataObject(int xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }


    public String getLineChartID() {
        return lineChartID;
    }

    public void setLineChartID(String lineChartID) {
        this.lineChartID = lineChartID;
    }




    public int getxValue() {
        return xValue;
    }

    public void setxValue(int xValue) {
        this.xValue = xValue;
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }





}
