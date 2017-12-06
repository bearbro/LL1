public class Main {
    private static final char Njump = 'ε';
    public static void main(String arg[]){
        String S="S";
        String GS[]={"S->AB|bC","A->"+Njump+"|b","B->aD","B->"+Njump,"C->AD|b","D->aS|c"};
        for (int i = 0; i <GS.length ; i++) {
            System.out.println(GS[i]);
        }
        WF G=new WF(GS,S);
        G.out();
    }
}
