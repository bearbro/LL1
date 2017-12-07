public class Main {
    private static final char Njump = 'ε';
    public static void main(String arg[]){
//        String S="S";
//        String GS[]={"S->AB|bC","A->"+Njump+"|b","B->aD","B->"+Njump,"C->AD|b","D->aS|c"};
//        String GS[]={"S->a|^|(T)","T->SN","N->,SN","N->"+Njump};
        String S="E";
//        String GS[]={"E->TA","A->+E|"+Njump,"T->FB","B->T|"+Njump,"F->PC","C->*C|"+Njump,"P->(E)|a|b|^"};
        String GS[]={"E->TA","A->+TA|"+Njump,"T->FB","B->*FB|"+Njump,"F->i|(E)"};
        for (int i = 0; i <GS.length ; i++) {
            System.out.println(GS[i]);
        }

        WF G=new WF(GS,S);
//        long endtime = System.nanoTime();

        G.calculationX();
//        long endtime = System.nanoTime();
//        long costTime = (endtime - begintime)/1000;
//        System.out.println("X耗时："+costTime+"微秒");
//
//        begintime = System.nanoTime();
        G.calculationFirst();
//        endtime = System.nanoTime();
//        costTime = (endtime - begintime)/1000;
//        System.out.println("First耗时："+costTime+"微秒");
//
//        begintime = System.nanoTime();
        G.calculationFollow();
//        endtime = System.nanoTime();
//        costTime = (endtime - begintime)/1000;
//        System.out.println("Follow耗时："+costTime+"微秒");

//        begintime = System.nanoTime();
        G.calculationSelect();
//        endtime = System.nanoTime();
//        costTime = (endtime - begintime)/1000;
        G.createLL1Table();
        G.out();
//        System.out.println("耗时："+costTime+"微秒");
        System.out.println(G.contains("i+i*i"));
        System.out.println(G.contains(""));
    }
}
