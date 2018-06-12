package de.androidpit.colorthief;

/**
 * Created by ooopic on 2017/12/12.
 */

import static de.androidpit.colorthief.MMCQ.MULT;
import static de.androidpit.colorthief.MMCQ.RSHIFT;
import static de.androidpit.colorthief.MMCQ.getColorIndex;

/**
 * 3D color space box.
 */
public class VBox {

    int r1;
    int r2;
    int g1;
    int g2;
    int b1;
    int b2;

    private final int[] histo;

    private int[] _avg;
    private Integer _volume;
    private Integer _count;

    public VBox(int r1, int r2, int g1, int g2, int b1, int b2, int[] histo) {
        this.r1 = r1;
        this.r2 = r2;
        this.g1 = g1;
        this.g2 = g2;
        this.b1 = b1;
        this.b2 = b2;

        this.histo = histo;
    }

    @Override
    public String toString() {
        return "r1: " + r1 + " / r2: " + r2 + " / g1: " + g1 + " / g2: " + g2 + " / b1: " + b1
                + " / b2: " + b2;
    }

    public int volume(boolean force) {
        if (_volume == null || force) {
            _volume = ((r2 - r1 + 1) * (g2 - g1 + 1) * (b2 - b1 + 1));
        }

        return _volume;
    }

    public int count(boolean force) {
        if (_count == null || force) {
            int npix = 0;
            int i, j, k, index;

            for (i = r1; i <= r2; i++) {
                for (j = g1; j <= g2; j++) {
                    for (k = b1; k <= b2; k++) {
                        index = getColorIndex(i, j, k);
                        npix += histo[index];
                    }
                }
            }

            _count = npix;
        }

        return _count;
    }

    @Override
    public VBox clone() {
        return new VBox(r1, r2, g1, g2, b1, b2, histo);
    }

    public int[] avg(boolean force) {
        if (_avg == null || force) {
            int ntot = 0;

            int rsum = 0;
            int gsum = 0;
            int bsum = 0;

            int hval, i, j, k, histoindex;

            for (i = r1; i <= r2; i++) {
                for (j = g1; j <= g2; j++) {
                    for (k = b1; k <= b2; k++) {
                        histoindex = getColorIndex(i, j, k);
                        hval = histo[histoindex];
                        ntot += hval;
                        rsum += (hval * (i + 0.5) * MULT);
                        gsum += (hval * (j + 0.5) * MULT);
                        bsum += (hval * (k + 0.5) * MULT);
                    }
                }
            }

            if (ntot > 0) {
                _avg = new int[] {~~(rsum / ntot), ~~(gsum / ntot), ~~(bsum / ntot)};
            } else {
                _avg = new int[] {~~(MULT * (r1 + r2 + 1) / 2), ~~(MULT * (g1 + g2 + 1) / 2),
                        ~~(MULT * (b1 + b2 + 1) / 2)};
            }
        }
        fix256(_avg);
        return _avg;
    }

    private int[] fix256(int[] _avg){
        for(int i = 0;i<_avg.length;i++){
            if(_avg[i]==256){
                _avg[i]=255;
            }
        }
        return _avg;
    }

    public boolean contains(int[] pixel) {
        int rval = pixel[0] >> RSHIFT;
        int gval = pixel[1] >> RSHIFT;
        int bval = pixel[2] >> RSHIFT;

        return (rval >= r1 && rval <= r2 && gval >= g1 && gval <= g2 && bval >= b1
                && bval <= b2);
    }

}
