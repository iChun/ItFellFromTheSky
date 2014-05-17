package itfellfromthesky.client.model;

import itfellfromthesky.client.render.RenderTransformer;
import itfellfromthesky.common.entity.EntityTransformer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelPigBase extends ModelBase
{
    //fields
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer snout;
    public ModelRenderer foot;

    public ModelPigBase()
    {
        textureWidth = 64;
        textureHeight = 32;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -4F, -4F, 8, 8, 8);
        head.setRotationPoint(0F, 0F, 0F);
        head.setTextureSize(64, 32);
        setRotation(head, 0F, 0F, 0F);
        body = new ModelRenderer(this, 28, 8);
        body.addBox(-5F, 0F, -8F, 10, 16, 8);
        body.setRotationPoint(0F, -2F, 2F);
        body.setTextureSize(64, 32);
        setRotation(body, 1.570796F, 0F, 0F);
        snout = new ModelRenderer(this, 16, 16);
        snout.addBox(-2F, 0F, 0F, 4, 3, 1);
        snout.setRotationPoint(0F, 0F, -5F);
        snout.setTextureSize(64, 32);
        setRotation(snout, 0F, 0F, 0F);
        foot = new ModelRenderer(this, 0, 16);
        foot.addBox(-2F, 0F, -2F, 4, 6, 4);
        foot.setRotationPoint(0F, 0F, 0F);
        foot.setTextureSize(64, 32);
        setRotation(foot, 0F, 0F, 0F);
    }

    public void render(EntityTransformer entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        GL11.glPushMatrix();

        GL11.glScalef(-1.0F, -1.0F, 1.0F);

        float time = (float)EntityTransformer.transformationTime * 1005F / 2000F;

        float progress = MathHelper.clamp_float((float)Math.pow(((float)entity.transformationProcess + f - time) / ((float)EntityTransformer.transformationTime * 1980F / 2000F - time), 0.5D), 0.0F, 1.0F);

        float scale = progress * 40F;

        GL11.glRotatef(entity.getOriginRot(), 0F, 1F, 0F);

        if((float)entity.transformationProcess + f > time)
        {
            GL11.glTranslatef(0.0F, -6F / 16F * scale, 0.0F);

            GL11.glPushMatrix();
            GL11.glTranslatef(3F / 16F * 40F, 6F / 16F * 40F, 5F / 16F * 40F);

            GL11.glScalef(scale, scale, scale);

            foot.render(f5);

            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(-3F / 16F * 40F, 6F / 16F * 40F, 5F / 16F * 40F);

            GL11.glScalef(scale, scale, scale);

            foot.render(f5);

            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef(3F / 16F * 40F, 6F / 16F * 40F, 17F / 16F * 40F);

            GL11.glScalef(scale, scale, scale);

            foot.render(f5);

            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(-3F / 16F * 40F, 6F / 16F * 40F, 17F / 16F * 40F);

            GL11.glScalef(scale, scale, scale);

            foot.render(f5);

            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();

        GL11.glRotatef(entity.getOriginRot(), 0F, -1F, 0F);

        GL11.glScalef(40F, 40F, 40F);

        progress = 0.0F;

        if(entity.transformationProcess + f > entity.transformationTime / 2 + 20)
        {
            progress = MathHelper.clamp_float(((float)(entity.transformationProcess + f) - ((float)entity.transformationTime / 2F + 20F)) / (((float)entity.transformationTime * 1800F / 2000F) - ((float)entity.transformationTime / 2F + 20F)), 0.0F, 1.0F);
        }

        GL11.glRotatef(90F + (-90F * progress), 0F, 1F, 0F);

        float rotYaw = RenderTransformer.interpolateRotation(entity.prevRotYaw, entity.rotYaw, f);
        GL11.glRotatef(-rotYaw + ((2 * rotYaw) * progress), 0F, 1F, 0F);

        GL11.glRotatef(RenderTransformer.interpolateRotation(entity.prevRotPitch, entity.rotPitch, f), 0F, 0F, 1F);

        head.render(f5);

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        scale = MathHelper.clamp_float((float)Math.pow(((float)entity.transformationProcess + f) / ((float)EntityTransformer.transformationTime / 2F), 0.5D) * 40F, 1.0F, 40F);

        GL11.glScalef(scale, scale, scale);
        body.render(f5);

        GL11.glPopMatrix();

        time = (float)EntityTransformer.transformationTime * 1980F / 2000F;

        if((float)entity.transformationProcess + f > time)
        {
            progress = MathHelper.clamp_float((float)Math.pow(((float)entity.transformationProcess + f - time) / ((float)EntityTransformer.transformationTime - time), 0.5D), 0.0F, 1.0F);

            scale = progress * 40F;

            GL11.glScalef(scale, scale, scale);

            snout.render(f5);
        }
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(EntityTransformer entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
    }

}
