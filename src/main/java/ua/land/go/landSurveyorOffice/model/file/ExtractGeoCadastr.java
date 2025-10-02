package ua.land.go.landSurveyorOffice.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractGeoCadastr {

    private String applicationNumber;
    private String cadNumber;
    private String parcelAddress;
    private String intendedPropose;
    private String parcelArea;
    private String parcelOwner;
    private String parcelOwnerDocument;
    private String fileName;
}
