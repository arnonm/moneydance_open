package com.moneydance.modules.features.tasemaya.jsondata;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FundListing {
    public String FundLongName;
    public String FundShortName;
    private int ManagerId;
    private String ManagerShortName;
    private String ManagerLongName;
    private int TrusteeId;
    private String TrusteeShortName;
    private String TrusteeLongName;
    private String Site;
    private String SharesExposureCd;
    private String SharesExposureDesc;
    private String ForeignCurrencyExposureCd;
    private String ForeignCurrencyExposureDesc;
    private float AssetValue;
    public LocalDateTime  AssetAsOfDate;
    private int InsertRates;
    private String TaxRouteDesc;
    private int TaxRouteCd;
    private boolean IsUnrestrictedFund;
    private boolean IsLeveragedFund;
    private boolean IsImitatingFund;
    private boolean IsInvertedExposureFund;
    private boolean IsBankPortfolioTracking;
    private boolean IsRegularDatesFund;
    private boolean IsFundsDenominated;
    private boolean IsForeignInvestmentFund;
    private boolean IsTaxFreeForForeignInvestment;
    private String ForeignCoinName;
    private String ForeignCoinCd;
    private int ProspectusReportCd;
    private LocalDateTime ProspectusPubDate;
    private int AnnualProspectusReportCd;
    private LocalDateTime AnnualProspectusPubDate;
    private int MonthlyReportCode;
    private int MonthlyReportMonth;
    private String MonthlyReportMonthDesc;
    private int MonthlyReportYear;
    private String EstimatedYield;
    private String EstimatedYieldDate;
    private String TradingHaltDesc;
    private String OfferingDay;
    private FundIndicator[] FundIndicators;
    private String[] Assets;
    private FundDisclosure[] DisclosureChart;
    private FundRisk[] AssetRisk;
    private FundAssetComposition AssetCompostion;
    private LocalDateTime DateValid;
    private int UnitValueIssued;
    private int UnitValueRedeemed;
    private String[] SecurityRedemptionData;
    private int FundId;
    private String FundTypeDesc;
    private int MagnaFundType;
    private String Logo;
    private boolean IsCandidate;
    private String MerLiqStatNm;
    private String MainClassificationCd;
    private String SecondaryClassificationCd;
    private String SubClassificationCd;
    private String MainClassification;
    private String SecondaryClassification;
    private String SubClassification;
    private String Classification;
    private String ExposureProfile;
    private String DistributeProfitPolicy;
    private float ManagementFee;
    private String VariableFee;
    private float TrusteeFee;
    private String SuccessFee;
    private String SharesExposureVal;
    private String ForeignCurrencyExposureVal;
    private int ClassificationCd;
    private String ClassificationDesc;

    private float PurchasePrice;
    private float SellPrice;
    private float CreationPrice;
    private float UnitValuePrice;
    private LocalDateTime UnitValueValidDate;
    private float DayYield;
    private boolean ShowDayYield;
    private String DailyRemark;
    private float PosNegYield;
    private LocalDateTime CorrectTradeDate;
    private boolean IsKosherFund;
    private float MachamTotal;
    private LocalDateTime DisclosureDateOfReport;
    private LocalDateTime RelevantDate;
    private int StockType;
    private int FundType;
    private String RegisteredCapitalPaid;
    private String PersonalFolderLink;
    private String FundSourceCd;
    private String TransitionAt;
    private boolean MonthShowYield;
    private float MonthYield;
    private float MonthPosNeg;
    private float MonthAverage;
    private String MonthDesc;
    private String MonthRemark;
    private boolean YearShowYield;
    private float YearYield;
    private float YearPosNeg;
    private float YearAverage;
    private String YearDesc;
    private String YearRemark;
    private boolean Last12MonthShowYield;
    private float Last12MonthYield;
    private float Last12MonthPosNeg;
    private float Last12MonthAverage;
    private String Last12MonthDesc;
    private String Last12MonthRemark;
    private boolean StandardDeviationShow;
    private float StandardDeviation;
    private float StandardDeviationPosNeg;
    private String StandardDeviationAverage;
    private String StandardDeviationDesc;
    private String StandardDeviationRemark;
    private int IsInternational;
    private String RedemptionFirstOrLast;
    private String RedemptionPeriod;
    private String RedemptionOther;
    private String RedemptionDays;
    private String MinBuyingUnit;
    private String MaxBuyingUnit;
    private String Title;
    private String EngTitle;
    private int TradeActiveDay;
    private String LastTradeDate;
    private String LastTrade;
    private String Icon;
    private FundMeta MetaTag;


    public  FundListing(String longName, String shortName, LocalDateTime asset) {
        this.FundLongName = longName;
        this.FundShortName = shortName;
        this.AssetAsOfDate = asset;
    }

    // public Map<String, String> toMap() {
    //     Map<String, String> map = parameters(new FundListing());
    //     return map;
    // }

    private static Map<String, String> parameters(Object obj) {
        Map<String, String> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj).toString());
           } catch (Exception e) {
            }
        }
        return map;
    }
}
