package greekfantasy.client.render.model;

import greekfantasy.entity.CyclopesEntity;

public class CyclopesModel<T extends CyclopesEntity> extends GiganteModel<T> {

  public CyclopesModel(float modelSize) {
    super(modelSize);
    head.texOffs(0, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 4.0F, 1.0F, modelSize);
  }  
}
