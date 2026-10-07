package com.ssakura49.sakuratinker.core.client.book;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.materials.TierRangeMaterialSectionTransformer;

public class STCrBookData extends BookData {
    public static final BookData SOUL_SAKURA_BOOK = BookLoader.registerBook(SakuraTinker.getResource("soul_sakura_book"),false,false);
    public static void intiBook(){
        SOUL_SAKURA_BOOK.addTransformer(ToolTagInjectorTransformer.INSTANCE);
        SOUL_SAKURA_BOOK.addTransformer(ModifierTagInjectorTransformer.INSTANCE);
        SOUL_SAKURA_BOOK.addTransformer(ToolSectionTransformer.INSTANCE);
        SOUL_SAKURA_BOOK.addRepository(new FileRepository(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "book/" + "soul_sakura_book")));
        SOUL_SAKURA_BOOK.addTransformer(BookTransformer.indexTranformer());
        SOUL_SAKURA_BOOK.addTransformer(TierRangeMaterialSectionTransformer.INSTANCE);
        SOUL_SAKURA_BOOK.addTransformer(BookTransformer.paddingTransformer());
    }
}
