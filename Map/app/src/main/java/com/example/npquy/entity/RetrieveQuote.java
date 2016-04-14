package com.example.npquy.entity;

import java.util.Date;

public class RetrieveQuote {
    private Double viaLat;
    private Double viaLong;
    private Integer custid;
    private String pick;
    private Double pickLat;
    private Double pickLong;
    private Double doffLat;
    private Double doffLong;
    private String doff;
    private String via;
    private String bookingdate; //datetime
    private Integer paq;
    private Integer bags;
    private String note;
    private Date returndate;
    private String rtnType;
    private Boolean childseat;
    private Boolean petfriendly;
    private Boolean executive;
    private String pickpostcode;
    private String droppostcode;
    private String viapostcode;

    public RetrieveQuote() {
        super();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bags == null) ? 0 : bags.hashCode());
        result = prime * result
                + ((bookingdate == null) ? 0 : bookingdate.hashCode());
        result = prime * result
                + ((childseat == null) ? 0 : childseat.hashCode());
        result = prime * result + ((custid == null) ? 0 : custid.hashCode());
        result = prime * result + ((doff == null) ? 0 : doff.hashCode());
        result = prime * result + ((doffLat == null) ? 0 : doffLat.hashCode());
        result = prime * result
                + ((doffLong == null) ? 0 : doffLong.hashCode());
        result = prime * result
                + ((droppostcode == null) ? 0 : droppostcode.hashCode());
        result = prime * result
                + ((executive == null) ? 0 : executive.hashCode());
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        result = prime * result + ((paq == null) ? 0 : paq.hashCode());
        result = prime * result
                + ((petfriendly == null) ? 0 : petfriendly.hashCode());
        result = prime * result + ((pick == null) ? 0 : pick.hashCode());
        result = prime * result + ((pickLat == null) ? 0 : pickLat.hashCode());
        result = prime * result
                + ((pickLong == null) ? 0 : pickLong.hashCode());
        result = prime * result
                + ((pickpostcode == null) ? 0 : pickpostcode.hashCode());
        result = prime * result
                + ((returndate == null) ? 0 : returndate.hashCode());
        result = prime * result + ((rtnType == null) ? 0 : rtnType.hashCode());
        result = prime * result + ((via == null) ? 0 : via.hashCode());
        result = prime * result + ((viaLat == null) ? 0 : viaLat.hashCode());
        result = prime * result + ((viaLong == null) ? 0 : viaLong.hashCode());
        result = prime * result
                + ((viapostcode == null) ? 0 : viapostcode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RetrieveQuote other = (RetrieveQuote) obj;
        if (bags == null) {
            if (other.bags != null)
                return false;
        } else if (!bags.equals(other.bags))
            return false;
        if (bookingdate == null) {
            if (other.bookingdate != null)
                return false;
        } else if (!bookingdate.equals(other.bookingdate))
            return false;
        if (childseat == null) {
            if (other.childseat != null)
                return false;
        } else if (!childseat.equals(other.childseat))
            return false;
        if (custid == null) {
            if (other.custid != null)
                return false;
        } else if (!custid.equals(other.custid))
            return false;
        if (doff == null) {
            if (other.doff != null)
                return false;
        } else if (!doff.equals(other.doff))
            return false;
        if (doffLat == null) {
            if (other.doffLat != null)
                return false;
        } else if (!doffLat.equals(other.doffLat))
            return false;
        if (doffLong == null) {
            if (other.doffLong != null)
                return false;
        } else if (!doffLong.equals(other.doffLong))
            return false;
        if (droppostcode == null) {
            if (other.droppostcode != null)
                return false;
        } else if (!droppostcode.equals(other.droppostcode))
            return false;
        if (executive == null) {
            if (other.executive != null)
                return false;
        } else if (!executive.equals(other.executive))
            return false;
        if (note == null) {
            if (other.note != null)
                return false;
        } else if (!note.equals(other.note))
            return false;
        if (paq == null) {
            if (other.paq != null)
                return false;
        } else if (!paq.equals(other.paq))
            return false;
        if (petfriendly == null) {
            if (other.petfriendly != null)
                return false;
        } else if (!petfriendly.equals(other.petfriendly))
            return false;
        if (pick == null) {
            if (other.pick != null)
                return false;
        } else if (!pick.equals(other.pick))
            return false;
        if (pickLat == null) {
            if (other.pickLat != null)
                return false;
        } else if (!pickLat.equals(other.pickLat))
            return false;
        if (pickLong == null) {
            if (other.pickLong != null)
                return false;
        } else if (!pickLong.equals(other.pickLong))
            return false;
        if (pickpostcode == null) {
            if (other.pickpostcode != null)
                return false;
        } else if (!pickpostcode.equals(other.pickpostcode))
            return false;
        if (returndate == null) {
            if (other.returndate != null)
                return false;
        } else if (!returndate.equals(other.returndate))
            return false;
        if (rtnType == null) {
            if (other.rtnType != null)
                return false;
        } else if (!rtnType.equals(other.rtnType))
            return false;
        if (via == null) {
            if (other.via != null)
                return false;
        } else if (!via.equals(other.via))
            return false;
        if (viaLat == null) {
            if (other.viaLat != null)
                return false;
        } else if (!viaLat.equals(other.viaLat))
            return false;
        if (viaLong == null) {
            if (other.viaLong != null)
                return false;
        } else if (!viaLong.equals(other.viaLong))
            return false;
        if (viapostcode == null) {
            if (other.viapostcode != null)
                return false;
        } else if (!viapostcode.equals(other.viapostcode))
            return false;
        return true;
    }

    public Double getViaLat() {
        return viaLat;
    }

    public void setViaLat(Double viaLat) {
        this.viaLat = viaLat;
    }

    public Double getViaLong() {
        return viaLong;
    }

    public void setViaLong(Double viaLong) {
        this.viaLong = viaLong;
    }

    public Integer getCustid() {
        return custid;
    }

    public void setCustid(Integer custid) {
        this.custid = custid;
    }

    public String getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(String bookingdate) {
        this.bookingdate = bookingdate;
    }

    public Integer getPaq() {
        return paq;
    }

    public void setPaq(Integer paq) {
        this.paq = paq;
    }

    public Integer getBags() {
        return bags;
    }

    public void setBags(Integer bags) {
        this.bags = bags;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getReturndate() {
        return returndate;
    }

    public void setReturndate(Date returndate) {
        this.returndate = returndate;
    }

    public Object getRtnType() {
        return rtnType;
    }

    public String getPick() {
        return pick;
    }

    public void setPick(String pick) {
        this.pick = pick;
    }

    public Double getPickLat() {
        return pickLat;
    }

    public void setPickLat(Double pickLat) {
        this.pickLat = pickLat;
    }

    public Double getPickLong() {
        return pickLong;
    }

    public void setPickLong(Double pickLong) {
        this.pickLong = pickLong;
    }

    public Double getDoffLat() {
        return doffLat;
    }

    public void setDoffLat(Double doffLat) {
        this.doffLat = doffLat;
    }

    public Double getDoffLong() {
        return doffLong;
    }

    public void setDoffLong(Double doffLong) {
        this.doffLong = doffLong;
    }

    public String getDoff() {
        return doff;
    }

    public void setDoff(String doff) {
        this.doff = doff;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public void setRtnType(String rtnType) {
        this.rtnType = rtnType;
    }

    public Boolean getChildseat() {
        return childseat;
    }

    public void setChildseat(Boolean childseat) {
        this.childseat = childseat;
    }

    public Boolean getPetfriendly() {
        return petfriendly;
    }

    public void setPetfriendly(Boolean petfriendly) {
        this.petfriendly = petfriendly;
    }

    public Boolean getExecutive() {
        return executive;
    }

    public void setExecutive(Boolean executive) {
        this.executive = executive;
    }

    public String getPickpostcode() {
        return pickpostcode;
    }

    public void setPickpostcode(String pickpostcode) {
        this.pickpostcode = pickpostcode;
    }

    public String getDroppostcode() {
        return droppostcode;
    }

    public void setDroppostcode(String droppostcode) {
        this.droppostcode = droppostcode;
    }

    public String getViapostcode() {
        return viapostcode;
    }

    public void setViapostcode(String viapostcode) {
        this.viapostcode = viapostcode;
    }

    @Override
    public String toString() {
        return "RetrieveQuote [viaLat=" + viaLat + ", viaLong=" + viaLong
                + ", custid=" + custid + ", pick=" + pick + ", pickLat="
                + pickLat + ", pickLong=" + pickLong + ", doffLat=" + doffLat
                + ", doffLong=" + doffLong + ", doff=" + doff + ", via=" + via
                + ", bookingdate=" + bookingdate + ", paq=" + paq + ", bags="
                + bags + ", note=" + note + ", returndate=" + returndate
                + ", rtnType=" + rtnType + ", childseat=" + childseat
                + ", petfriendly=" + petfriendly + ", executive=" + executive
                + ", pickpostcode=" + pickpostcode + ", droppostcode="
                + droppostcode + ", viapostcode=" + viapostcode + "]";
    }


}
