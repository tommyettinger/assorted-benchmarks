package net.adoptopenjdk.bumblebench.examples;

/**
 * Created by Tommy Ettinger on 12/24/2019.
 */
public class Grid2 {
    public int x, y;
    public Grid2(){
        x = 0;
        y = 0;
    }
    public Grid2(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grid2 grid2 = (Grid2) o;

        return x == grid2.x && y == grid2.y;
    }

    @Override
    public int hashCode() {
        //HashSet_Grid2_Bench score: 30749414.000000 (30.75M 1724.1%)
        //                uncertainty:   1.2%
//        return (x * 0xC13F + y * 0x91E1);
        //HashSet_Grid2_Bench score: 29735680.000000 (29.74M 1720.8%)
        //                uncertainty:   2.6%
//        return (x * 0x06328F + y * 0x1E7B1D);
        //HashSet_Grid2_Bench score: 29051166.000000 (29.05M 1718.5%)
        //                uncertainty:   2.9%
//        return (int)((x * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >>> 32);
        
//        final long result = (x * 0x9E3779B97F4A7C15L + y) * 0x9E3779B97F4A7C15L;
//        return (int)(result >>> 32);
//		final int xx = x << 1 ^ x >> 31;
//		final int yy = y << 1 ^ y >> 31;
//		////Rosenberg-Strong Pairing Function
//		////assigns numbers to (x,y) pairs, assigning bigger numbers to bigger shells (the shell is max(x,y)).
//		return xx + (xx > yy ? xx * xx + xx - yy : yy * yy);
//        //Cantor Pairing Function
//        //also assigns numbers to (x,y) pairs, but shells are triangular stripes instead of right angles.
////        return yy + ((xx + yy) * (xx + yy + 1) >> 1);

        //HashSet_Grid2_Bench score: 6273411.500000 (6.273M 1565.2%)
        //                uncertainty:  13.5%
        // actually good data set:
        //HashSet_Grid2_Bench score: 30022642.000000 (30.02M 1721.7%)
        //                uncertainty:   2.3%
//        final long xx = x + 0x80000000L;
//        final long yy = y + 0x80000000L;

		final long xx = (x << 1 ^ x >> 31) + 0x80000000L;
		final long yy = (y << 1 ^ y >> 31) + 0x80000000L;
        return (int) (xx + (xx > yy ? xx * xx + xx - yy : yy * yy));

//        return (int)(yy + ((xx + yy) * (xx + yy + 1) >> 1));
        
//        final int prime = 31;
//        final int prime = 53;
//        final int prime = 0xDE4D;
//        int result = 1;
//        result = prime * result + this.x;
//        result = prime * result + this.y;
//        return result;

    }
}
