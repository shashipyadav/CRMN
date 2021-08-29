package com.example.myapplication.projectdetails;


public class MemberLedgerModel
{
    private String MEMBER_NAME;

    private String UNIT_CODE;

    private String PARTICULARS;

    private String RECEIVED_AMT;

    private String VOUCHER_NO;

    private String CHEQUE_NO;

    private String DATE;

    private String DUE_AMT;

    private String VOUCHER_TYPE;

    private String CHEQUE_DT;

    private String RECEIPT_NO;

    private String MEMBER_CODE;

    private String BANK_NAME;

    public String getMEMBER_NAME ()
    {
        return MEMBER_NAME;
    }

    public void setMEMBER_NAME (String MEMBER_NAME)
    {
        this.MEMBER_NAME = MEMBER_NAME;
    }

    public String getUNIT_CODE ()
    {
        return UNIT_CODE;
    }

    public void setUNIT_CODE (String UNIT_CODE)
    {
        this.UNIT_CODE = UNIT_CODE;
    }

    public String getPARTICULARS ()
    {
        return PARTICULARS;
    }

    public void setPARTICULARS (String PARTICULARS)
    {
        this.PARTICULARS = PARTICULARS;
    }

    public String getRECEIVED_AMT ()
    {
        return RECEIVED_AMT;
    }

    public void setRECEIVED_AMT (String RECEIVED_AMT)
    {
        this.RECEIVED_AMT = RECEIVED_AMT;
    }

    public String getVOUCHER_NO ()
    {
        return VOUCHER_NO;
    }

    public void setVOUCHER_NO (String VOUCHER_NO)
    {
        this.VOUCHER_NO = VOUCHER_NO;
    }

    public String getCHEQUE_NO ()
    {
        return CHEQUE_NO;
    }

    public void setCHEQUE_NO (String CHEQUE_NO)
    {
        this.CHEQUE_NO = CHEQUE_NO;
    }

    public String getDATE ()
    {
        return DATE;
    }

    public void setDATE (String DATE)
    {
        this.DATE = DATE;
    }

    public String getDUE_AMT ()
    {
        return DUE_AMT;
    }

    public void setDUE_AMT (String DUE_AMT)
    {
        this.DUE_AMT = DUE_AMT;
    }

    public String getVOUCHER_TYPE ()
    {
        return VOUCHER_TYPE;
    }

    public void setVOUCHER_TYPE (String VOUCHER_TYPE)
    {
        this.VOUCHER_TYPE = VOUCHER_TYPE;
    }

    public String getCHEQUE_DT ()
    {
        return CHEQUE_DT;
    }

    public void setCHEQUE_DT (String CHEQUE_DT)
    {
        this.CHEQUE_DT = CHEQUE_DT;
    }

    public String getRECEIPT_NO ()
    {
        return RECEIPT_NO;
    }

    public void setRECEIPT_NO (String RECEIPT_NO)
    {
        this.RECEIPT_NO = RECEIPT_NO;
    }

    public String getMEMBER_CODE ()
    {
        return MEMBER_CODE;
    }

    public void setMEMBER_CODE (String MEMBER_CODE)
    {
        this.MEMBER_CODE = MEMBER_CODE;
    }

    public String getBANK_NAME ()
    {
        return BANK_NAME;
    }

    public void setBANK_NAME (String BANK_NAME)
    {
        this.BANK_NAME = BANK_NAME;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [MEMBER_NAME = "+MEMBER_NAME+", UNIT_CODE = "+UNIT_CODE+", PARTICULARS = "+PARTICULARS+", RECEIVED_AMT = "+RECEIVED_AMT+", VOUCHER_NO = "+VOUCHER_NO+", CHEQUE_NO = "+CHEQUE_NO+", DATE = "+DATE+", DUE_AMT = "+DUE_AMT+", VOUCHER_TYPE = "+VOUCHER_TYPE+", CHEQUE_DT = "+CHEQUE_DT+", RECEIPT_NO = "+RECEIPT_NO+", MEMBER_CODE = "+MEMBER_CODE+", BANK_NAME = "+BANK_NAME+"]";
    }
}

