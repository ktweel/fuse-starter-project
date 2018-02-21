package org.galatea.starter.entrypoint.messagecontracts;

import com.google.protobuf.InvalidProtocolBufferException;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.springframework.stereotype.Component;

@Component
public class TradeAgreementTranslator {

  public TradeAgreement translateToAgreement(byte[] buffer) throws InvalidProtocolBufferException {
    TradeAgreementMessage message = TradeAgreementMessage.parseFrom(buffer);

    return TradeAgreement.builder().id(message.getId()).instrument(message.getInstrument())
        .internalParty(message.getInternalParty()).externalParty(message.getExternalParty())
        .buySell(message.getBuySell()).qty(message.getQty()).build();
  }

  public byte[] translateFromAgreement(TradeAgreement agreement) {
    if (agreement == null)
      return TradeAgreementMessage.getDefaultInstance().toByteArray();

    TradeAgreementMessage message = TradeAgreementMessage.newBuilder().setId(agreement.getId())
        .setInstrument(agreement.getInstrument()).setInternalParty(agreement.getInternalParty())
        .setExternalParty(agreement.getExternalParty()).setBuySell(agreement.getBuySell())
        .setQty(agreement.getQty()).build();

    return message.toByteArray();
  }
}
