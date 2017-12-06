import java.util.ArrayList;

public class YJ {//语句
    String left;
    String right;

    //left->right
    public YJ(String a) {
        String b[] = a.split("->");
        left = b[0];
        right = b[1];
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public boolean isSimple() {
        if (right.indexOf("|") == -1)
            return true;
        else
            return false;
    }
    public ArrayList<YJ> toSimple(){
        ArrayList<YJ> yjList=new  ArrayList<YJ>();
        String b[] = right.split("\\|");
        for (int i = 0; i <b.length ; i++) {
           // System.out.println(b[i]);
            YJ ib=new YJ(left+"->"+b[i]);
            yjList.add(ib);
        }
        return yjList;
    }
    public String out(){
        return left+"->"+right;
    }
}
