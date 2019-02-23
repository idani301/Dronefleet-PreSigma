package eyesatop.util.android.dtm;

import java.io.File;
import java.io.IOException;

import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.dtm.DtmProvierASC;

public class DtmProviderASCAndroid extends DtmProvierASC {

    public DtmProviderASCAndroid() throws IOException, IllegalArgumentException {
        super(EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.DTM_ASC,false));
    }
}
