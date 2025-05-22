class Account:
    def __init__(self, account_id, bank, currency) -> None:
        self.account_id = account_id
        self.bank=bank
        self.currency=currency

    @classmethod
    def from_json(cls, json_data):
        return cls(
            account_id=json_data['accountId'],
            bank=json_data['bank'],
            currency=json_data['currency'])