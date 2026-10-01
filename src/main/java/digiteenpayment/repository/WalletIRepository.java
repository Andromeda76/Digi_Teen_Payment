package digiteenpayment.repository;

import digiteenpayment.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WalletIRepository extends JpaRepository<Wallet, Long> {

}
