import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalMath {
    private final int accuracy; 
    private final BigDecimal accuracyNum; 

    private final BigDecimal _105_095; 
    private final BigDecimal log_105_095; // log(1.05/0.95)
    private BigDecimal log10; // log(10)
    private final BigDecimal atan05; // arctan(0.5)
    private final BigDecimal PI2; // PI/2

    // Constants
    private static final BigDecimal PI = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");
    private static final BigDecimal E = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");

    // Constructor
    public BigDecimalMath(int ac) { // Set up the basic values
        accuracy = ac + 2; // Calculation accuracy is 2 digits higher than the set accuracy

        accuracyNum = BigDecimal.ONE.divide(BigDecimal.TEN.pow(accuracy)); // 1/10^-accuracy
        _105_095 = new BigDecimal("1.05").divide(new BigDecimal("0.95"),accuracy,BigDecimal.ROUND_HALF_EVEN); // Calculate 1.05/0.95
        log_105_095 = log_095_105(_105_095); // Calculate ln(1.05/0.95)
        atan05 = atan(new BigDecimal("0.5")); // Calculate arctan(0.5)
        PI2 = PI.divide(new BigDecimal("2"),accuracy,BigDecimal.ROUND_HALF_EVEN); // Calculate PI/2

        log10 = new BigDecimal("0");
        for(int i=0;i<10;i++){
            log10 = log10.add(log_095_105(new BigDecimal("1.25")));
        }
        log10 = log10.add(log_095_105(new BigDecimal("1.073741824")));
    }

    // Methods
    public BigDecimal atan(BigDecimal x) {
        boolean isMinus = false; // Check if the parameter is negative
        boolean isGt1 = false; // Check if the parameter is greater than 1
        boolean isGt05 = false; // Check if the parameter is greater than 0.5

        if(x.signum() == -1){ // If the parameter is negative, change the sign of the parameter and the result
            x = x.negate();
            isMinus = true;
        }

        if(x.compareTo(BigDecimal.ONE) > 0){ // If the parameter is greater than 1, take the reciprocal
            x = BigDecimal.ONE.divide(x,accuracy,BigDecimal.ROUND_HALF_EVEN);
            isGt1 = true;
        }

        if(x.compareTo(new BigDecimal("0.5")) > 0){ // If the parameter is greater than 0.5, convert it to the form (x-0.5)/(x+0.5)
            BigDecimal fm = x.multiply(new BigDecimal("0.5")).add(BigDecimal.ONE);
            x = x.subtract(new BigDecimal("0.5")).divide(fm,accuracy,BigDecimal.ROUND_HALF_EVEN);
            isGt05 = true;
        }

        BigDecimal res = new BigDecimal("0"); 
        BigDecimal term = new BigDecimal("0"); 
        int i = 0;

        do{ 
            term = x.pow(4*i+1).divide(new BigDecimal(4*i+1),accuracy,BigDecimal.ROUND_HALF_EVEN);
            term = term.subtract(x.pow(4*i+3).divide(new BigDecimal(4*i+3),accuracy,BigDecimal.ROUND_HALF_EVEN));
            res = res.add(term);
            i++;
        }while(term.compareTo(accuracyNum) > 0); 

        if(isGt05){ 
            res = atan05.add(res);
        }
        if(isGt1){ 
            res = PI2.subtract(res);
        }
        if(isMinus){ 
            res = res.negate();
        }

        return res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); 
    }

    public BigDecimal acos(BigDecimal x){

        return PI2.subtract(asin(x)).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); 
    }

    public BigDecimal asin(BigDecimal x){

        boolean isMinus = false;

        if(x.signum() == -1){ 
            x = x.negate();
            isMinus = true;
        }

        if(x.compareTo(BigDecimal.ONE) > 0){ 
            System.out.println("��ֵ������,������Ϊ[-1,1]");
            return BigDecimal.ZERO;
        }
        else if(x.compareTo(BigDecimal.ONE) == 0){ 
            return PI2;
        }

        BigDecimal res = pow(x,new BigDecimal("2"));
        res = BigDecimal.ONE.subtract(res);
        res = pow(res,new BigDecimal("0.5"));
        res = x.divide(res,accuracy,BigDecimal.ROUND_HALF_EVEN);
        res = atan(res);

        return (isMinus ? res.negate() : res).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
    public BigDecimal tan(BigDecimal x){
        if(x.abs().compareTo(PI2) >= 0){ 
            System.out.println("��ֵ������,������Ϊ(-PI/2,PI/2)");
            return BigDecimal.ZERO;
        }

        return sin(x).divide(cos(x),accuracy,BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal cos(BigDecimal x){

        boolean isMinus = false;

        x = x.abs();
        int quotient = x.divideToIntegralValue(PI2).intValue(); 

        switch(quotient % 4){ 
            case 1:x = PI.subtract(x);isMinus = true;break; 
            case 2:isMinus = true;break; 
            case 3:x = PI.subtract(x);break; 
        }

        BigDecimal res = new BigDecimal("0");
        BigDecimal term = new BigDecimal("0");
        int i = 0;

        do{ 
            term = x.pow(2*i).divide(fac(2*i),accuracy,BigDecimal.ROUND_HALF_EVEN);
            res = res.add(i%2==1 ? term.negate() : term);
            i++;
        }while(term.compareTo(accuracyNum) > 0);

        return (isMinus ? res.negate() : res).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal sin(BigDecimal x){

        boolean isMinus = false;

        if(x.compareTo(BigDecimal.ZERO) < 0){
            x = x.negate();
            isMinus = !isMinus;
        }

        int quotient = x.divideToIntegralValue(PI2).intValue();
        x = x.remainder(PI);

        switch(quotient % 4){
            case 1:x = PI.subtract(x);break;
            case 2:isMinus = !isMinus;break;
            case 3:x = PI.subtract(x);isMinus = !isMinus;break;
        }

        BigDecimal res = new BigDecimal("0");
        BigDecimal term = new BigDecimal("0");
        int i = 0;

        do{
            term = x.pow(2*i+1).divide(fac(2*i+1),accuracy,BigDecimal.ROUND_HALF_EVEN);
            res = res.add(i%2==1 ? term.negate() : term);
            i++;
        }while(term.compareTo(accuracyNum) > 0);

        return (isMinus ? res.negate() : res).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal toRadians(BigDecimal deg){
        deg = deg.divide(new BigDecimal("180"),accuracy,BigDecimal.ROUND_HALF_EVEN);
        return deg.multiply(PI).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal toDegrees(BigDecimal rad){
        rad = rad.multiply(new BigDecimal("180"));
        return rad.divide(PI,accuracy,BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal pow(BigDecimal a,BigDecimal x){

        boolean isMinus = false;

        if(x.signum() == -1){ 
            x = x.negate();
            isMinus = true;
        }

        try{ 
            x.intValueExact();
        }catch(Exception e){ 
            BigDecimal xlna = x.multiply(log(a)).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); 
            BigDecimal res = new BigDecimal("0");
            BigDecimal term = new BigDecimal("0");
            int i = 0;
    
            do{
                term = xlna.pow(i).divide(fac(i),accuracy,BigDecimal.ROUND_HALF_EVEN);
                res = res.add(term);
                i++;
            }while(term.abs().compareTo(accuracyNum) > 0); 
    
            return isMinus ? BigDecimal.ONE.divide(res,accuracy,BigDecimal.ROUND_HALF_EVEN) : res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
        }

        BigDecimal res = a.pow(x.intValue()).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); 
        return isMinus ? BigDecimal.ONE.divide(res,accuracy,BigDecimal.ROUND_HALF_EVEN) : res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
    public BigDecimal log(BigDecimal a,BigDecimal x){
        //��aΪ��x�Ķ���=log(x)/log(a)
        BigDecimal res = log(x).divide(log(a),accuracy,BigDecimal.ROUND_HALF_EVEN);
        return res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
    public BigDecimal log(BigDecimal x){
        if(x.compareTo(BigDecimal.ZERO) <= 0){
            System.out.println("�����������0");
            return BigDecimal.ZERO;
        }

        BigDecimal res = new BigDecimal("0");
        int ln10Count = 0; //10�������ۼӱ���
        int ln1_1Count = 0; //1.1�������ۼӱ���

        while(x.compareTo(new BigDecimal("5")) > 0){ //��������5��10����С����ѭ��ִ��xlog10�Σ���(��10Ϊ��x�Ķ���)��
            x = x.divide(BigDecimal.TEN,accuracy,BigDecimal.ROUND_HALF_EVEN); //���ò�������10
            ln10Count++; //�ۼ�һ�δ��������һ��10����С
        }
        while(x.compareTo(new BigDecimal("0.5")) < 0){ //����С��0.5,10���Ŵ󣬸�ѭ��ִ��xlog0.1����(��0.1Ϊ��x�Ķ���)��
            x = x.multiply(BigDecimal.TEN); //����������10
            ln10Count--; //�ۼ�һ�δ��������һ��10���Ŵ�
        }

        while(x.compareTo(new BigDecimal("1.05")) > 0){ //��������1.05��1.1����С����ѭ�����ִ��24�Σ���10/(1.1^24)<1.05
            x = x.divide(_105_095,accuracy,BigDecimal.ROUND_HALF_EVEN);
            ln1_1Count++; //�ۼ�һ�δ��������һ��1.1����С
        }
        while(x.compareTo(new BigDecimal("0.95")) < 0){ //����С��0.95��1.1���Ŵ󣬸�ѭ�����ִ��7�Σ���0.5*(1.1^7)>0.95
            x = x.multiply(_105_095);
            ln1_1Count--; //�ۼ�һ�δ��������һ��1.1���Ŵ�
        }
        x = x.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //������������λ

        res = log10.multiply(new BigDecimal(ln10Count)); //����ln10Count*ln(10)��ֵ
        res = res.add(log_105_095.multiply(new BigDecimal(ln1_1Count))); //����ln1_1Count*ln(1.1)��ֵ

        return res.add(log_095_105(x)).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //�������ŵ�[0.95, 1.05]�����ln(x)��ֵ
    }

    private BigDecimal log_095_105(BigDecimal x){
        if(x.compareTo(BigDecimal.ZERO) <= 0){ //ln(x)�в���x�������0
            System.out.println("�����������0");
            return BigDecimal.ZERO;
        }
        BigDecimal y = (x.subtract(BigDecimal.ONE)).divide(x.add(BigDecimal.ONE),accuracy,BigDecimal.ROUND_HALF_EVEN); //�ȼ����y
        BigDecimal res = new BigDecimal("0"); //�ۼӵĽ��
        BigDecimal term = new BigDecimal("0"); //��ѭ���м���ÿһ��ۼ�
        int i = 0; // �ۼӱ���
        do{
            term = y.pow(2*i).divide(new BigDecimal(2*i+1),accuracy,BigDecimal.ROUND_HALF_EVEN); //����չ����ͨ�ʽ�����i��
            res = res.add(term); //ÿ���ۼ�
            i++; //��i��
        }while(term.compareTo(accuracyNum) > 0); //���ۼӵ���С�ھ���Ҫ��ʱ�˳��������Ѵﵽ�趨����
        res = res.multiply(y);
        res = res.multiply(new BigDecimal("2")); //����ǰ���2y     
        return res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //�����������Ϊaccuracy
    }
    public BigDecimal fac(int n){
        if(n < 0){//����û�н׳�
            System.out.println("����û�н׳�");
            return BigDecimal.ZERO;
        }
        BigInteger res = new BigInteger("1");
        BigInteger Bn = BigInteger.valueOf(n); //��nתΪBigInteger����

        while(Bn.compareTo(BigInteger.ONE) > 0){ //����1��ʱ���۳�
            res = res.multiply(Bn);
            Bn = Bn.subtract(BigInteger.ONE); //�Լ�1
        }
        return new BigDecimal(res); 
    }
    public int getAccuracy(){ 
        return accuracy;
    }
    public BigDecimal getPI(){ 
        return PI.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
    public BigDecimal getE(){ 
        return E.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
}

