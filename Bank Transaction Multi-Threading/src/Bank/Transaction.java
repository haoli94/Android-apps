package Bank;

public class Transaction {
    public static final Transaction NULLTRANS = new Transaction(-1,0,0);
    private final int withdrawID;
    private final int depositID;
    private final int amount;

    public Transaction(int fromID,int toID, int amount){
        this.withdrawID = fromID;
        this.depositID = toID;
        this.amount = amount;
    }
    public int getWithdrawID(){
        return withdrawID;
    }
    public int getDepositID() {
        return depositID;
    }
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transferring from User Id = " + getWithdrawID() + ", to User ID = " + getDepositID()
                + ", with amount = " + getAmount();
    }
}

