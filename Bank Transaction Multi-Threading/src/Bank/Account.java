package Bank;
public class Account {
    private int transactionsCount;
    private final int Id;
    private int curBalance;
    // Data Attributes

    public Account(int Id,int Balance) {
        this.Id = Id;
        this.transactionsCount = 0;
        this.curBalance = Balance;
    }

    public int getId() {
        return this.Id;
    }
    public int getTransactionsCount() {
        return this.transactionsCount;
    }
    public int getCurrentBalance() {
        return this.curBalance;
    }
    public void Deposit(int amount) {
        this.curBalance += amount;
        this.transactionsCount++;
    }
    public void withdraw(int amount) {
        this.curBalance -= amount;
        this.transactionsCount++;
    }

    @Override
    public String toString() {
        return "AccountId = " + getId() + ", TransactionsCount = " + getTransactionsCount()
                + ", CurrentBalance = " + getCurrentBalance();
    }


}
