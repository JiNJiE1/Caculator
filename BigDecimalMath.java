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
            System.out.println("该值无意义,定义域为[-1,1]");
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
            System.out.println("该值无意义,定义域为(-PI/2,PI/2)");
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
        //以a为底x的对数=log(x)/log(a)
        BigDecimal res = log(x).divide(log(a),accuracy,BigDecimal.ROUND_HALF_EVEN);
        return res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN);
    }
    public BigDecimal log(BigDecimal x){
        if(x.compareTo(BigDecimal.ZERO) <= 0){
            System.out.println("参数必须大于0");
            return BigDecimal.ZERO;
        }

        BigDecimal res = new BigDecimal("0");
        int ln10Count = 0; //10倍缩放累加变量
        int ln1_1Count = 0; //1.1倍缩放累加变量

        while(x.compareTo(new BigDecimal("5")) > 0){ //参数大于5，10倍缩小，该循环执行xlog10次，即(以10为底x的对数)次
            x = x.divide(BigDecimal.TEN,accuracy,BigDecimal.ROUND_HALF_EVEN); //将该参数除以10
            ln10Count++; //累加一次代表进行了一次10倍缩小
        }
        while(x.compareTo(new BigDecimal("0.5")) < 0){ //参数小于0.5,10倍放大，该循环执行xlog0.1，即(以0.1为底x的对数)次
            x = x.multiply(BigDecimal.TEN); //将参数乘以10
            ln10Count--; //累加一次代表进行了一次10倍放大
        }

        while(x.compareTo(new BigDecimal("1.05")) > 0){ //参数大于1.05，1.1倍缩小，该循环最多执行24次，即10/(1.1^24)<1.05
            x = x.divide(_105_095,accuracy,BigDecimal.ROUND_HALF_EVEN);
            ln1_1Count++; //累加一次代表进行了一次1.1倍缩小
        }
        while(x.compareTo(new BigDecimal("0.95")) < 0){ //参数小于0.95，1.1倍放大，该循环最多执行7次，即0.5*(1.1^7)>0.95
            x = x.multiply(_105_095);
            ln1_1Count--; //累加一次代表进行了一次1.1倍放大
        }
        x = x.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //重新设置舍入位

        res = log10.multiply(new BigDecimal(ln10Count)); //计算ln10Count*ln(10)的值
        res = res.add(log_105_095.multiply(new BigDecimal(ln1_1Count))); //计算ln1_1Count*ln(1.1)的值

        return res.add(log_095_105(x)).setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //计算缩放到[0.95, 1.05]区间的ln(x)的值
    }

    private BigDecimal log_095_105(BigDecimal x){
        if(x.compareTo(BigDecimal.ZERO) <= 0){ //ln(x)中参数x必须大于0
            System.out.println("参数必须大于0");
            return BigDecimal.ZERO;
        }
        BigDecimal y = (x.subtract(BigDecimal.ONE)).divide(x.add(BigDecimal.ONE),accuracy,BigDecimal.ROUND_HALF_EVEN); //先计算出y
        BigDecimal res = new BigDecimal("0"); //累加的结果
        BigDecimal term = new BigDecimal("0"); //在循环中计算每一项，累加
        int i = 0; // 累加变量
        do{
            term = y.pow(2*i).divide(new BigDecimal(2*i+1),accuracy,BigDecimal.ROUND_HALF_EVEN); //按照展开的通项公式计算第i项
            res = res.add(term); //每项累加
            i++; //第i项
        }while(term.compareTo(accuracyNum) > 0); //当累加的项小于精度要求时退出，代表已达到设定精度
        res = res.multiply(y);
        res = res.multiply(new BigDecimal("2")); //乘以前面的2y     
        return res.setScale(accuracy,BigDecimal.ROUND_HALF_EVEN); //将结果精度设为accuracy
    }
    public BigDecimal fac(int n){
        if(n < 0){//负数没有阶乘
            System.out.println("负数没有阶乘");
            return BigDecimal.ZERO;
        }
        BigInteger res = new BigInteger("1");
        BigInteger Bn = BigInteger.valueOf(n); //将n转为BigInteger类型

        while(Bn.compareTo(BigInteger.ONE) > 0){ //大于1的时候累乘
            res = res.multiply(Bn);
            Bn = Bn.subtract(BigInteger.ONE); //自减1
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

