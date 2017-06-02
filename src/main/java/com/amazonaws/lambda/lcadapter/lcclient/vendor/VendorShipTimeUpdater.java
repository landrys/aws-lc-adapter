package com.amazonaws.lambda.lcadapter.lcclient.vendor;

import java.util.Iterator;
import java.util.List;

import com.amazonaws.lambda.lcadapter.functions.LCVendorAdapter;
import com.landry.aws.lambda.dynamo.domain.VendorShipTime;

public class VendorShipTimeUpdater
{
	private List<Vendor> vendors;
	private Long nextVendorShipTimeId;
	private boolean persistedChanges = false;

	public void doWork()
	{
		Iterator<Vendor> it = vendors.iterator();
		System.out.println("Working on " + vendors.size() + " vendors...");
		while (it.hasNext())
		{
			Vendor v = it.next();
			Integer vendorId = v.getId();
			Boolean archived = v.getArchived();
			if (archived == null || !archived)
			{
				List<VendorShipTime> vsts = LCVendorAdapter.vstDao.findByVendorId(vendorId);
				if (!vsts.isEmpty())
				{
					System.out.println("Found " + vsts.size() + " vendor ship times to check for changes.");
					if (changed(vsts.get(0), v.getName()))
						for (VendorShipTime vst : vsts)
						{
							vst.setName(v.getName());
							LCVendorAdapter.vstDao.write(vst);
							System.out.println("Changed vendor ship time entry to: " + vst);
						}
					else
						System.out.println("No changes to vendor detected.");

				}
				else
				{

					System.out.println("Vendor " + v.getName() + " has no vendor ship time entry.");
					// Need to create a new one with defaults
					VendorShipTime vst = new VendorShipTime();
					vst.setId(nextVendorShipTimeId);
					vst.setVendorId(v.getId());
					vst.setName(v.getName());
					vst.setIsBike(false);
					vst.setWeeklyOrder(false);
					vst.setDropShipToStore(false);
					LCVendorAdapter.vstDao.write(vst);
					System.out.println("Created vendor ship time entry " + vst);
					nextVendorShipTimeId++;
				}

			}
			else
			{
				System.out.println("Vendor " + v.getName() + " has been archived.");
				List<VendorShipTime> vsts = LCVendorAdapter.vstDao.findByVendorId(vendorId);
				System.out.println("There are " + vsts.size() + " vendor ship times entries to delete.");
				if (!vsts.isEmpty())
					for (VendorShipTime vst : vsts)
					{
						LCVendorAdapter.vstDao.delete(vst);
						System.out.println("Deleted vendor ship time entry " + vst);
					}
			}

		}

	}

	private boolean changed( VendorShipTime vendorShipTime, String name )
	{
		if (vendorShipTime.getName().equalsIgnoreCase(name)) {
			return false;
		} else {
			System.out.println("Name change [from:to]:[" 
		            + vendorShipTime.getName() + ":" + name + "]");
			this.persistedChanges = true;
			return true;
		}
	}

	public static class Builder
	{
		private List<Vendor> vendors;
		private Long nextVendorShipTimeId;

		public Builder vendors( List<Vendor> vendors )
		{
			this.vendors = vendors;
			return this;
		}

		public Builder nextVendorShipTimeId( Long nextVendorShipTimeId )
		{
			this.nextVendorShipTimeId = nextVendorShipTimeId;
			return this;
		}

		public VendorShipTimeUpdater build()
		{
			return new VendorShipTimeUpdater(this);
		}
	}

	private VendorShipTimeUpdater(Builder builder)
	{
		this.vendors = builder.vendors;
		this.nextVendorShipTimeId = builder.nextVendorShipTimeId;
	}

	public boolean persistedChanges()
	{
		return persistedChanges;
	}

}
