import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WF {
    private static final char Njump = 'ε';
    Set<String> VN;//非终结符
    Set<String> VT;//终结符集
    Set<YJ> P;//规则集
    String S;//开始符
    Map<String, Integer> X;
    Map<String, Set> First;
    Map<String, Set> FirstR;
    Map<String, Set> Follow;
    Map<YJ,Set> Select;
    public WF(Set<String> VN, Set<String> VT, Set<YJ> p, String s) {
        this.VN = VN;
        this.VT = VT;
        P = p;
        S = s;
    }

    public WF(String[] ip, String S) {
        long begintime = System.nanoTime();
        this.S = S;
        VN = new HashSet();
        VT = new HashSet();
        P = new HashSet();
        for (int i = 0; i < ip.length; i++) {
            YJ p = new YJ(ip[i]);
            VN.add(p.getLeft());
            if (p.isSimple()) {
                P.add(p);
                String pr = ((YJ) p).getRight();
                for (int j = 0; j < pr.length(); j++) {
                    VT.add(String.valueOf(pr.charAt(j)));
                }
            } else {
                for (Object sp : p.toSimple()) {
                    P.add((YJ) sp);
                    String spr = ((YJ) sp).getRight();
                    for (int j = 0; j < spr.length(); j++) {
                        VT.add(String.valueOf(spr.charAt(j)));
                    }
                }
            }
        }
        VT.removeAll(VN);
        VT.remove(String.valueOf(Njump));

    }

    public void calculationFirst() {
        //1
        First =new HashMap<>();
        for (Object ivt:VT) {
            Set<String> ivtF=new HashSet<String>();
            ivtF.add((String)ivt);
            First.put((String)ivt,ivtF);
        }

        //2 3
        for (Object ivn:VN) {
            Set<String> ivnF=new HashSet<String>();
            First.put((String)ivn,ivnF);
        }
        Set<YJ> p2 = new HashSet<>();//右部第一个字符为非总结符的产生式
        for (Object ip:P) {
            String ipLeft=((YJ)ip).getLeft();
            String ipRight=((YJ)ip).getRight();
            String fR = ipRight.substring(0, 1);
            if (!VN.contains(fR)) {//2 3
                First.get(ipLeft).add(fR);
            } else {
                p2.add((YJ)ip);
            }
        }
        //4 5
        boolean changed = true;
        while (changed) {
            changed=false;
            for (Object ip : p2) {
                String ipLeft = ((YJ) ip).getLeft();
                String ipRight = ((YJ) ip).getRight();
                Set<String> ipLFirst = First.get(ipLeft);
                if(upFirst(ipLFirst,ipRight)){
                    changed=true;
                }
            }
        }
        //6 右部的FirstR
//        Map
                FirstR =new HashMap<>();
        for (Object ip : P) {
            String ipRight = ((YJ) ip).getRight();
            int pRL = ipRight.length();
            Set<String> ipRF=new HashSet<String>();
            upFirst(ipRF,ipRight);
            FirstR.put(ipRight,ipRF);
        }
//        First.putAll(FirstR);
 }

    private boolean upFirst(Set<String> ipRFirst,String ipR){//根据ipR更新ipRFirst
        boolean changed=false;
        int pRL = ipR.length();
        for (int i = 0; i < pRL; i++) {
            String fR = ipR.substring(i, i + 1);
            if (VN.contains(fR)) {
                for (Object ifRFirst : First.get(fR)) {
                    if (!((String) ifRFirst).equals(String.valueOf(Njump))) {
                        if (ipRFirst.add((String) ifRFirst))
                            changed = true;
                    }
                }
                if (X.get(fR) != 1) {//fR!->kong
                    break;
                }
                if (i == pRL - 1) {
                    if (ipRFirst.add(String.valueOf(Njump)))
                        changed = true;
                }
            } else {
                if (ipRFirst.add(fR))
                    changed = true;
                break;
            }
        }
        return changed;
    }

    public void calculationFollow(){
        Follow=new HashMap<>();
        for (Object ivn:VN) {
            Set<String> ivnFo=new HashSet<String>();
            Follow.put((String)ivn,ivnFo);
        }
        //1
        Follow.get(S).add("#");
        //2

        boolean changed=true;//标记Follow是否变化
        Set<String> adFo=new HashSet<String>();
        while (changed){
            changed=false;
            for (Object ip:P) {
                String ipLeft=((YJ)ip).getLeft();
                String ipRight=((YJ)ip).getRight();
                int pRL = ipRight.length();
                for (int i = 0; i <pRL ; i++) {
                    String fR = ipRight.substring(i, i + 1);
                    if (VN.contains(fR)) {
                        if(i!=pRL-1){
                            adFo.clear();
                            upFirst(adFo,ipRight.substring(i + 1,pRL));
                            if(adFo.contains(String.valueOf(Njump))){
                                adFo.remove(String.valueOf(Njump));
                                adFo.addAll(Follow.get(ipLeft));
                            }
                            if(Follow.get(fR).addAll(adFo)){
                                changed=true;
                            }
                        }else{
                            if(Follow.get(fR).addAll(Follow.get(ipLeft))){
                                changed=true;
                            }
                        }
                    }
                }
            }
        }
    }

    public void calculationSelect(){
        Select=new HashMap<>();
        for (Object ip:P) {
            String ipLeft=((YJ)ip).getLeft();
            String ipRight=((YJ)ip).getRight();
            Set select=new HashSet();
            select.addAll(FirstR.get(ipRight));
            if(select.contains(String.valueOf(Njump))){
                select.remove(String.valueOf(Njump));
                select.addAll(Follow.get(ipLeft));
            }
            Select.put((YJ)ip,select);
        }
    }
    public void calculationX() {
        //计算非终结符能否推出空串的数组(Map)
        //1
        X = new HashMap();//-1 未确定 0不能 1能到'ε'
        for (Object key : VN) {
            X.put((String) key, -1);
        }
        //2.1
        Set<YJ> p2 = new HashSet<>();
        for (Object ip : P) {
            YJ i = (YJ) ip;
            boolean haveVT = false;
            for (Object ivt : VT) {
                int idx = i.getRight().indexOf((String) ivt);
                if (idx != -1) {
                    haveVT = true;
                    break;
                }
            }
            if (!haveVT) {
                p2.add(i);
            }
        }
        Set<String> left = new HashSet<>();
        for (Object ip : p2) {
            left.add(((YJ) (ip)).getLeft());
        }
        for (Object key : VN) {
            if (!left.contains((String) key)) {
                X.put((String) key, 0);
            }
        }

        //2.2
        Set<String> left2 = new HashSet<>();
        for (Object ip : p2) {
            if (((YJ) (ip)).getRight().equals(String.valueOf(Njump))) {
                String key = ((YJ) (ip)).getLeft();
                left2.add(key);
                X.put(key, 1);
            }
        }
        Set<YJ> p3 = new HashSet<>();
        for (Object ip : p2) {
            if (left2.contains(((YJ) (ip)).getLeft())) {
                p3.add((YJ) ip);
            }
        }
        p2.removeAll(p3);
        //3
        boolean changed = true;
        while (changed) {
            changed = false;
            p3.clear();
            for (Object ip : p2) {
                String ipRight = ((YJ) (ip)).getRight();
                for (int i = 0; i < ipRight.length(); i++) {
                    int ix = X.get(String.valueOf(ipRight.charAt(i)));
                    if (ix == 1) {//3.1
                        if (ix < ipRight.length() - 1) {
                            continue;
                        } else {
                            String ipLeft = ((YJ) (ip)).getLeft();
                            System.out.println(ipLeft);
                            X.put(ipLeft, 1);
                            for (Object ipp : p2) {
                                if (ipLeft.equals(((YJ) (ipp)).getLeft())) {
                                    p3.add((YJ) ipp);
                                }
                            }
                            changed = true;
                        }
                    } else if (ix == 0) {//3.2
                        p3.add((YJ) ip);
                        String ipLeft = ((YJ) (ip)).getLeft();
                        int haveipLeftN = 0;
                        for (Object ipp : p2) {
                            if (ipLeft.equals(((YJ) (ipp)).getLeft())) {
                                haveipLeftN++;
                            }
                        }
                        if (haveipLeftN == 1) {
                            X.put(ipLeft, 0);
                            changed = true;
                        }
                        break;
                    }
                }

            }
            p2.removeAll(p3);
        }
    }

    public void out() {
        System.out.println("VN:");
        for (Object iVN : VN) {
            System.out.println(iVN);
        }
        System.out.println("VT:");
        for (Object iVT : VT) {
            System.out.println(iVT);
        }
        System.out.println("P:");
        for (Object ip : P) {
            System.out.println(((YJ) ip).toString());
        }
        System.out.println("S:");
        System.out.println(S);
        System.out.println("X:");
        for (Object x : X.entrySet()) {
            System.out.println(x.toString());
        }
        System.out.println("First:");
        for (Object x : First.entrySet()) {
            System.out.println(x.toString());
        }
        System.out.println("FirstR:");
        for (Object x : FirstR.entrySet()) {
            System.out.println(x.toString());
        }
        System.out.println("Follow:");
        for (Object x : Follow.entrySet()) {
            System.out.println(x.toString());
        }
        System.out.println("Select:");
        for (Object x : Select.entrySet()) {
            System.out.println(x.toString());
        }
    }
}
